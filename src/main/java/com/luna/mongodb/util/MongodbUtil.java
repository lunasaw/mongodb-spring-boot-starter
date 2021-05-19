package com.luna.mongodb.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luna.common.page.Page;
import com.luna.mongodb.anno.IncIdDocument;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * @author luna
 */
@Component
public class MongodbUtil {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存对象List到指定集合中
     * <p>
     * 也可以在实体类上使用@Document(collection=“集合名称”)指定集合名称，未指定则默认实体类的类名为集合名称
     *
     * @param entities
     */
    public List<?> save(String collName, List<?> entities) {
        ArrayList<Object> list = Lists.newArrayList();
        for (Object entity : entities) {
            list.add(saveData(collName, entity));
        }
        return list;
    }

    public List<?> save(List<?> entities) {
        return save(StringUtils.EMPTY, entities);
    }

    /**
     * 保存单个对象到指定集合中
     *
     * @param collName 集合名称
     * @param entity 实体名称
     */
    public Object save(String collName, Object entity) {
        return saveData(collName, entity);
    }

    public Object save(Object entity) {
        return save(StringUtils.EMPTY, entity);
    }

    /**
     * 判断是否存在
     *
     * @param entity
     * @param collName
     * @param map
     * @return
     */
    public boolean exist(Class<?> entity, String collName, Map<String, Object> map) {
        Query query = getQuery(map);
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.exists(query, entity);
        }
        return mongoTemplate.exists(query, entity, collName);
    }

    public boolean exist(Class<?> entity, Map<String, Object> map) {
        return exist(entity, StringUtils.EMPTY, map);
    }

    /**
     * 查询返回指定字段
     *
     * @param fields 需要返回的指定字段 eg: fields.add("runTime");
     * @param clazz 数据实体类class
     * @param collName 集合名称
     * @param map Map<查询条件key,查询条件value> eg: map.put("describe", "查询用户信息");
     * @return
     */
    public List<?> findDesignField(Class<?> clazz, List<String> fields, List<String> exclude, Map<String, Object> map,
        String collName) {
        Query query = getQuery(map);
        for (String field : fields) {
            query.fields().include(field);
        }
        for (String field : exclude) {
            query.fields().exclude(field);
        }
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.find(query, clazz);
        }
        return mongoTemplate.find(query, clazz, collName);
    }

    public List<?> findDesignField(Class<?> clazz, Map<String, Object> map) {
        return findDesignField(clazz, new ArrayList<>(), new ArrayList<>(), map, StringUtils.EMPTY);
    }

    public List<?> findDesignField(Class<?> clazz, List<String> fields, List<String> exclude, Map<String, Object> map) {
        return findDesignField(clazz, fields, exclude, map, StringUtils.EMPTY);
    }

    public List<?> findDesignField(Class<?> clazz, Map<String, Object> map, boolean returnId) {
        List<String> fields = new ArrayList<>();
        if (!returnId) {
            fields.add(IncIdDocument.ID);
        }
        return findDesignField(clazz, new ArrayList<>(), fields, map, StringUtils.EMPTY);
    }

    /**
     * 查询指定集合中的所有数据
     *
     * @param entity 数据实体类
     * @param collName 集合名称
     */
    public List<?> findAll(Class<?> entity, String collName) {
        return mongoTemplate.findAll(entity, collName);
    }

    /**
     * 模糊查询 根据 key 可以到 collName 中进行模糊查询 并排序
     *
     * @param param 匹配的参数名称
     * @param key 模糊搜索关键字
     * @param collName 集合名称
     * @param sortField 排序字段
     * @param direction Direction.desc /asc 倒序/正序
     * @return java.lang.Object
     **/
    public List<?> findLikeByParam(Class<?> clazz, String param, String key, String collName, String sortField,
        Sort.Direction direction) {
        Pattern pattern = Pattern.compile("^.*" + key + ".*$", Pattern.CASE_INSENSITIVE);
        ImmutableMap<String, Map<String, Object>> regex = ImmutableMap.of(param, ImmutableMap.of("regex", pattern));
        return findRangeByParam(clazz, collName, Maps.newHashMap(), sortField, direction, regex);

    }

    public List<?> findLikeByParam(Class<?> clazz, String param, String key, String sortField,
        Sort.Direction direction) {
        return findLikeByParam(clazz, param, key, StringUtils.EMPTY, sortField, direction);
    }

    /**
     * 向指定集合设置索引
     *
     * @param collName 集合名称
     * @param indexName 索引名称
     * @param map map.put("添加索引的字段",Direction.ASC/DESC)
     */
    public void createIndex(String collName, String indexName, Map<String, Sort.Direction> map) {
        Index index = new Index().named(indexName);
        for (String key : map.keySet()) {
            index.on(key, map.get(key));
        }
        mongoTemplate.indexOps(collName).ensureIndex(index);
    }

    /**
     * 获取指定集合中的索引信息
     *
     * @param collName 集合名称
     * @return
     */
    public List<IndexInfo> getIndexInfo(String collName) {
        return mongoTemplate.indexOps(collName).getIndexInfo();
    }

    /**
     * 根据索引名称删除索引
     *
     * @param indexName 索引名称
     * @param collName 集合名称
     */
    public void removeIndexByName(String collName, String indexName) {
        mongoTemplate.indexOps(collName).dropIndex(indexName);
    }

    /**
     * 删除指定集合中得所有索引
     *
     * @param collName 集合名称
     */
    public void removeIndexByName(String collName) {
        mongoTemplate.indexOps(collName).dropAllIndexes();
    }

    /**
     * 根据指定key 和value到指定collName集合中删除数据
     *
     * @param map Map<"查询条件key"，查询条件值> map
     * @param collName
     */
    public long removeAllByParam(Class<?> clazz, String collName, Map<String, Object> map) {
        Query query = getQuery(map);
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.remove(query, clazz).getDeletedCount();
        }

        if (clazz == null) {
            return mongoTemplate.remove(query, collName).getDeletedCount();
        }
        return mongoTemplate.remove(query, clazz, collName).getDeletedCount();
    }

    public long removeAllByParam(Class<?> clazz, Map<String, Object> map) {
        return removeAllByParam(clazz, StringUtils.EMPTY, map);
    }

    public long removeAllByParam(String collName, Map<String, Object> map) {
        return removeAllByParam(null, collName, map);
    }

    /**
     * 根据指定条件查询 并排序
     *
     * @param clazz 数据对象
     * @param map Map<"查询条件key"，查询条件值> map
     * @param collName 集合名称
     * @return
     */
    public List<?> findSortByParam(Class<?> clazz, String collName, Map<String, Object> map, String sortField,
        Sort.Direction direction) {
        Query query = getQuery(map);
        query = sort(query, sortField, direction);
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.find(query, clazz);
        }
        return mongoTemplate.find(query, clazz, collName);
    }

    public List<?> findSortByParam(Class<?> clazz, Map<String, Object> map, String sortField,
        Sort.Direction direction) {
        return findSortByParam(clazz, StringUtils.EMPTY, map, sortField, direction);
    }

    /**
     * 默认Id升序排列
     *
     * @param clazz
     * @param map
     * @return
     */
    public List<?> findSortByParam(Class<?> clazz, Map<String, Object> map) {
        return findSortByParam(clazz, StringUtils.EMPTY, map, IncIdDocument.ID, Sort.Direction.ASC);
    }

    /**
     * 排序条件构建
     *
     * @param query
     * @param sortField
     * @param direction
     * @return
     */
    public Query sort(Query query, String sortField, Sort.Direction direction) {
        return query.with(Sort.by(direction, sortField));
    }

    /**
     * 范围查询
     * <p>
     * 查询大于等于begin 小于等于end范围内条件匹配得数据并排序
     *
     * @param clazz 数据对象
     * @param collName 集合名称
     * @param map Map<"查询条件key"，查询条件值> map
     * @param sortField 排序字段
     * @param direction 排序方式 Direction.asc / Direction.desc
     * @param rangeMap 示例： lt小于 lte 小于等于 gt大于 gte大于等于 eq等于 ne不等于
     * <p>
     * Criteria rangeCriteria=Criteria.where("createDate").gte(begin).lte(end));
     * <p>
     * createDate:数据库中的时间字段，gegin:起始时间 end:结束时间
     * @return
     */
    public List<?> findRangeByParam(Class<?> clazz, String collName, Map<String, Object> map,
        String sortField, Sort.Direction direction, Map<String, Map<String, Object>> rangeMap) {
        Query query = getQuery(map);
        List<AtomicReference<Criteria>> list = new ArrayList<>(rangeMap.size());
        rangeMap.forEach((k, v) -> {
            AtomicReference<Criteria> criteria = new AtomicReference<>(new Criteria(k));
            list.add(criteria);
            v.forEach((k1, v1) -> criteria.set(getRangeCriteria(criteria.get(), k1, v1)));
        });
        list.forEach(item -> query.addCriteria(item.get()));
        sort(query, sortField, direction);
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.find(query, clazz);
        }
        return mongoTemplate.find(query, clazz, collName);
    }

    public Criteria getRangeCriteria(Criteria criteria, String condition, Object rangeValue) {
        if (criteria == null) {
            return new Criteria();
        }
        switch (condition) {
            case "lt":
                criteria = criteria.lt(rangeValue);
                break;
            case "lte":
                criteria = criteria.lte(rangeValue);
                break;
            case "gt":
                criteria = criteria.gt(rangeValue);
                break;
            case "gte":
                criteria = criteria.gte(rangeValue);
                break;
            case "eq":
                criteria = criteria.is(rangeValue);
                break;
            case "ne":
                criteria = criteria.ne(rangeValue);
                break;
            case "regex":
                criteria = criteria.regex((Pattern)rangeValue);
                break;
            default:
                return criteria;
        }
        return criteria;
    }

    /**
     * 根据指定key value到指定集合中查询匹配得数量
     *
     * @param clazz
     * @return
     */
    public long count(Class<?> clazz, String collName, Map<String, Object> map) {
        Query query = getQuery(map);
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.count(query, clazz);
        }
        return mongoTemplate.count(query, clazz, collName);
    }

    public long count(Class<?> clazz, Map<String, Object> map) {
        return count(clazz, StringUtils.EMPTY, map);
    }

    /**
     * 在指定范围内查询匹配条件的数量
     *
     * @param clazz 数据实体类
     * @param collName 集合名称
     * @param map 查询条件map
     * @param rangeCriteria 范围条件 Criteria rangeCriteria= Criteria.where("数据库字段").gt/gte（起始范围）.lt/lte（结束范围）
     * @return
     */
    public Long countRangeCondition(Class<?> clazz, String collName, Criteria rangeCriteria, Map<String, Object> map) {
        Query query = getQuery(map);
        query.addCriteria(rangeCriteria);
        return mongoTemplate.count(query, clazz, collName);
    }

    /**
     * 指定集合 根据条件查询出符合的第一条数据
     *
     * @param clazz 数据对象
     * @param map 条件map Map<条件key,条件value> map
     * @param collName 集合名
     * @return
     */
    public Object findSortFirst(Class<?> clazz, Map<String, Object> map, String collName, String sortField,
        Sort.Direction direction) {
        Query query = getQuery(map);
        query = sort(query, sortField, direction);
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.findOne(query, clazz);
        }
        return mongoTemplate.findOne(query, clazz, collName);
    }

    public Object findSortFirst(Class<?> clazz, Map<String, Object> map) {
        return findSortFirst(clazz, map, StringUtils.EMPTY, IncIdDocument.ID, Sort.Direction.ASC);
    }

    /**
     * 指定集合 修改数据，且修改所找到的所有数据 修改所有匹配得数据
     *
     * @param criteriaMap Map<修改条件 key数组,修改条件 value数组>
     * @param map Map<修改内容 key数组,修改内容 value数组>
     * @param collName 集合名
     */
    public long updateMulti(Class<?> clazz, String collName, Map<String, Object> criteriaMap, Map<String, Object> map) {
        Query query = getQuery(criteriaMap);
        Update update = new Update();

        for (String key : map.keySet()) {
            update.set(key, map.get(key));
        }
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.updateMulti(query, update, clazz).getModifiedCount();
        }
        return mongoTemplate.updateMulti(query, update, clazz, collName).getModifiedCount();
    }

    public long updateMulti(Class<?> clazz, Map<String, Object> map) {
        return updateMulti(clazz, StringUtils.EMPTY, new HashMap<>(), map);
    }

    /**
     * 指定集合 修改数据，修改第一条数据
     *
     * @param criteriaMap Map<修改条件 key数组,修改条件 value数组>
     * @param map Map<修改内容 key数组,修改内容 value数组>
     * @param collName 集合名
     */
    public long updateFirst(Class<?> clazz, String collName, Map<String, Object> criteriaMap, Map<String, Object> map) {
        Query query = getQuery(criteriaMap);
        Update update = new Update();

        for (String key : map.keySet()) {
            update.set(key, map.get(key));
        }
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.updateFirst(query, update, clazz).getModifiedCount();
        }
        return mongoTemplate.updateFirst(query, update, clazz, collName).getModifiedCount();
    }

    public long updateFirst(Class<?> clazz, Map<String, Object> criteriaMap, Map<String, Object> map) {
        return updateFirst(clazz, StringUtils.EMPTY, criteriaMap, map);
    }

    /**
     * 对某字段做sum求和
     *
     * @param clazz 数据实体类
     * @param map Map<查询条件key,查询条件value> map
     * @param collName 集合名称
     * @param sumField 求和字段
     * @param rangeCriteria 范围条件
     * @return Criteria rangeCriteria = Criteria.where(字段).gt(起始范围).lt(结束范围)
     */
    public Object findSum(Class<?> clazz, Map<String, Object> map, String collName, String sumField,
        Criteria rangeCriteria) {
        MatchOperation match = null;
        Criteria criteria = getCriteria(map);
        if (criteria != null) {
            match = Aggregation.match(criteria);
        }
        GroupOperation count = Aggregation.group().sum(sumField).as(sumField);
        return mongoTemplate.aggregate(Aggregation.newAggregation(match, count), collName, clazz).getMappedResults();
    }

    public Page<?> findSortPageCondition(Class<?> clazz, Map<String, Object> map,
        int pageNo, int pageSize, Sort.Direction direction, String sortField) {
        return findSortPageCondition(clazz, mongoTemplate.getCollectionName(clazz), map, pageNo, pageSize, direction,
            sortField);
    }

    public Page<?> findSortPageCondition(Class<?> clazz, Map<String, Object> map, int pageNo, int pageSize) {
        return findSortPageCondition(clazz, mongoTemplate.getCollectionName(clazz), map, pageNo, pageSize,
            Sort.Direction.ASC, IncIdDocument.ID);
    }

    public Page<?> findSortPageCondition(Class<?> clazz, int pageNo, int pageSize) {
        return findSortPageCondition(clazz, mongoTemplate.getCollectionName(clazz), new HashMap<>(), pageNo, pageSize,
            Sort.Direction.ASC, IncIdDocument.ID);
    }

    /**
     * 分页查询
     *
     * @param clazz 数据实体类
     * @param collName 集合名称
     * @param map Map<"查询条件key"，查询条件值> map 若 keys/values 为null,则查询集合中所有数据
     * @param pageNo 当前页
     * @param pageSize 当前页数据条数
     * @param direction Direction.Desc/ASC 排序方式
     * @param sortField 排序字段
     * @return
     */
    public Page<?> findSortPageCondition(Class<?> clazz, String collName, Map<String, Object> map,
        int pageNo, int pageSize, Sort.Direction direction, String sortField) {

        Criteria criteria = getCriteria(map);
        long count;
        if (criteria == null) {
            count = mongoTemplate.count(new Query(), clazz, collName);
        } else {
            count = mongoTemplate.count(new Query(criteria), clazz, collName);
        }
        int pages = (int)Math.ceil((double)count / (double)pageSize);
        if (pageNo <= 0 || pageNo > pages) {
            pageNo = 1;
        }
        int skip = pageSize * (pageNo - 1);
        Query query = new Query().skip(skip).limit(pageSize);
        query = sort(query, sortField, direction);
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        List<?> list = mongoTemplate.find(query, clazz, collName);
        Page<?> page = new Page();
        page.initPage((int)count, pageNo, pageSize);
        page.setList(list);
        return page;
    }

    private Query getQuery(Map<String, Object> map) {
        Criteria criteria = getCriteria(map);
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        return query;
    }

    private Object saveData(String collName, Object entity) {
        if (StringUtils.isEmpty(collName)) {
            return mongoTemplate.save(entity, mongoTemplate.getCollectionName(entity.getClass()));
        } else {
            return mongoTemplate.save(entity, collName);
        }
    }

    private Criteria getCriteria(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        int i = 0;
        Criteria criteria = null;
        for (String key : map.keySet()) {
            if (i == 0) {
                criteria = Criteria.where(key).is(map.get(key));
                i++;
            } else {
                criteria.and(key).is(map.get(key));
            }
        }
        return criteria;
    }

}
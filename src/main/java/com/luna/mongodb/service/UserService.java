package com.luna.mongodb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.luna.common.page.Page;
import com.luna.mongodb.anno.IncIdDocument;
import com.luna.mongodb.entity.UserDO;
import com.luna.mongodb.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author luna
 */
@Service
public class UserService {

    @Autowired
    private MongodbUtil mongodbUtil;

    public UserDO get(Long id) {
        return (UserDO)mongodbUtil.findSortFirst(UserDO.class, ImmutableMap.of(IncIdDocument.ID, id));
    }

    public UserDO save(UserDO userDO) {
        return (UserDO)mongodbUtil.save(userDO);
    }

    public List<UserDO> saveBatch(List<UserDO> list) {
        return (List<UserDO>)mongodbUtil.save(list);
    }

    public List<UserDO> listByEntity(UserDO userDO) {
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(userDO));
        return (List<UserDO>)mongodbUtil.findDesignField(UserDO.class, map);
    }

    public Page<UserDO> listPageByEntity(int page, int size, UserDO userDO) {
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(userDO));
        return (Page<UserDO>)mongodbUtil.findSortPageCondition(UserDO.class, map, page, size);
    }

    public Page<UserDO> listPage(int page, int size) {
        return (Page<UserDO>)mongodbUtil.findSortPageCondition(UserDO.class, null, page, size);
    }

    public long update(UserDO userDO) {
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(userDO));
        return mongodbUtil.updateFirst(UserDO.class, ImmutableMap.of(IncIdDocument.ID, userDO.getId()), map);
    }

    public long updateBatch(List<UserDO> list) {
        long count = 0;
        for (UserDO userDO : list) {
            Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(userDO));
            count += mongodbUtil.updateFirst(UserDO.class, ImmutableMap.of(IncIdDocument.ID, userDO.getId()), map);
        }
        return count;
    }

    public long deleteById(Long id) {
        return mongodbUtil.removeAllByParam(UserDO.class, ImmutableMap.of(IncIdDocument.ID, id));
    }

    public long deleteByEntity(UserDO userDO) {
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(userDO));
        return mongodbUtil.removeAllByParam(UserDO.class, map);
    }

    public long deleteByIds(List<Long> ids) {
        return ids.stream()
            .mapToLong(id -> mongodbUtil.removeAllByParam(UserDO.class, ImmutableMap.of(IncIdDocument.ID, id))).sum();
    }

    public long countAll() {
        return mongodbUtil.count(UserDO.class, ImmutableMap.of());
    }

    public long countByEntity(UserDO userDO) {
        Map<String, Object> map = JSONObject.parseObject(JSON.toJSONString(userDO));
        return mongodbUtil.count(UserDO.class, map);
    }
}

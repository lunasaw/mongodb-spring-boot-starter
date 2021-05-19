package com.luna.mongodb.listener;

import com.luna.mongodb.anno.AutoIncId;
import com.luna.mongodb.anno.IncIdDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 原理就是维护一个 collection 来保存自增ID每个
 * 其余的每一个 collection 在这个表里面就是一条数据，并记录一个数
 * 每次插入之间通过事件触发 然后通过 findAndModify 直接更新值，并且返回最新值
 * findAndModify 操作本身是原子操作所以不需要当心并发问题
 *
 * @author luna
 * @since 2020/3/19
 **/
@Component
public class SaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        ReflectionUtils.doWithFields(event.getSource().getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            if (field.isAnnotationPresent(AutoIncId.class) && field.get(event.getSource()) == null) {
                field.set(event.getSource(), getId(event.getCollectionName()));
            }
        });
    }

    /**
     * 获取自增id
     * 这边是利用mongo的findAndModify的原子性实现的
     * 也可以使用redis来实现
     */
    private Long getId(final String collName) {
        final Query query = new Query().addCriteria(new Criteria(IncIdDocument.COLLECTION_NAME).is(collName));
        final Update update = new Update();
        update.inc(IncIdDocument.SEQ_ID, 1);
        final FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        final IncIdDocument sequence = mongoTemplate.findAndModify(query, update, options, IncIdDocument.class);
        return sequence.getSeqId();
    }
}

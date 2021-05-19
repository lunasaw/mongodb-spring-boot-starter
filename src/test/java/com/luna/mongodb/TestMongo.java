package com.luna.mongodb;

import com.google.common.collect.ImmutableMap;
import com.luna.common.page.Page;
import com.luna.mongodb.anno.IncIdDocument;
import com.luna.mongodb.entity.UserDO;
import com.luna.mongodb.util.MongodbUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 茶轴的青春
 * @Description TODO 测试MongoDB工具类
 * @date 2020/7/24 15:55
 */
@SpringBootTest
public class TestMongo {

    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongodbUtil   mongoDBUtil;

    @Test
    public void atest() {
        String collectionName = mongoTemplate.getCollectionName(UserDO.class);
        System.out.println(collectionName);
        boolean exist =
            mongoDBUtil.exist(UserDO.class, UserDO.class.getName(), ImmutableMap.of("username", "demoData34"));
        System.out.println(exist);
    }
}
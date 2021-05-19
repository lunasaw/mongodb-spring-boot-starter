package com.luna.mongodb.config;
import com.luna.mongodb.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author luna@mac
 */
@Configuration
public class MongoAutoConfiguration {

    @Autowired
    private final MongodbUtil mongodbUtil;

    public MongoAutoConfiguration(MongodbUtil mongodbUtil) {
        this.mongodbUtil = mongodbUtil;
    }
}

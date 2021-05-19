package com.luna.mongodb.config;

import com.luna.mongodb.listener.SaveMongoEventListener;
import com.luna.mongodb.util.MongodbUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luna@mac
 */
@Configuration
public class MongoAutoConfiguration {

    @Bean
    public MongodbUtil mongodbUtil() {
        return new MongodbUtil();
    }

    @Bean
    public SaveMongoEventListener mongoEventListener() {
        return new SaveMongoEventListener();
    }
}

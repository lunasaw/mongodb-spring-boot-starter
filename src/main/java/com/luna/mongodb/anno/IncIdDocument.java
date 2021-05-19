package com.luna.mongodb.anno;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * 自增Id 实体
 *
 * @author hsf
 * @since 2020/3/19
 **/
public class IncIdDocument implements Serializable {

    private static final long  serialVersionUID = -9141159479430770232L;

    /**
     * id 静态变量
     */
    public final static String ID               = "_id";

    /**
     * seqId 静态变量
     */
    public final static String SEQ_ID           = "seqId";

    /**
     * collectionName 静态变量
     */
    public final static String COLLECTION_NAME  = "mongodb";

    /**
     * 默认ID
     */
    @Id
    private ObjectId           id;

    /**
     * 自增ID
     */
    private long               seqId;

    /**
     * document名称 即生成的是哪个document的ID
     */
    private String             collectionName;

    public ObjectId getId() {
        return id;
    }

    public IncIdDocument setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public long getSeqId() {
        return seqId;
    }

    public IncIdDocument setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public IncIdDocument setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }
}

package com.sencorsta.ids.core.database.mongo;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Objects;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * mongo操作模板
 *
 * @author daibin
 */
@Beta
public class MongoTemplate {
    static CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
    static CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
    static MongoClient mongoClient;
    String dataBaseName;

    public MongoTemplate(MongoConfig mongoConfig) {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(mongoConfig.getUrl());
        }
        this.dataBaseName = mongoConfig.getDataBaseName();
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * 查询一条记录
     *
     * @param filter           过滤
     * @param projectionFields 部分查询字段
     * @param clazz            返回类型
     * @return 结果
     * @see Filters
     */
    public <T> T findOne(String collectionName, Bson filter, Bson projectionFields, Class<T> clazz) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<T> collection = database.getCollection(collectionName, clazz).withCodecRegistry(pojoCodecRegistry);
        return collection.find(filter).projection(projectionFields).first();
    }

    /**
     * 查询一条记录
     *
     * @param filter 过滤
     * @param clazz  返回类型
     * @return 结果
     * @see Filters
     */
    public <T> T findOne(String collectionName, Bson filter, Class<T> clazz) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<T> collection = database.getCollection(collectionName, clazz).withCodecRegistry(pojoCodecRegistry);
        return collection.find(filter).first();
    }

    /**
     * 查询多条记录
     *
     * @param filter 过滤
     * @param clazz  返回类型
     * @return 结果列表
     * @see Filters
     */
    public <T> List<T> findAll(String collectionName, Bson filter, Class<T> clazz) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<T> collection = database.getCollection(collectionName, clazz).withCodecRegistry(pojoCodecRegistry);
        FindIterable<T> ts = collection.find(filter);
        return ts.into(Lists.newArrayList());
    }


    /**
     * 对继承BaseEntity的对象插入
     */
    public <T extends BaseEntity> InsertOneResult insertOneTable(T obj) {
        String collectionName = obj.getClass().getSimpleName();
        obj.update();
        return insertOne(collectionName, obj);
    }

    /**
     * 对继承BaseEntity的对象查找
     */
    public <T extends BaseEntity> T findOneTable(Bson filter, Class<T> clazz) {
        String collectionName = clazz.getSimpleName();
        return findOne(collectionName, filter, clazz);
    }

    /**
     * 插入多条记录
     *
     * @param obj 元素
     * @return InsertManyResult
     * @see Filters
     */
    public <T> InsertOneResult insertOne(String collectionName, T obj) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<T> collection = database.getCollection(collectionName, ClassUtil.getClass(obj)).withCodecRegistry(pojoCodecRegistry);
        return collection.insertOne(obj);
    }

    /**
     * 插入多条记录
     *
     * @param objs 元素列表
     * @return InsertManyResult
     * @see Filters
     */
    public InsertManyResult insertMany(String collectionName, List<Object> objs) {
        if (ObjectUtil.isEmpty(objs)) {
            return null;
        }
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Object> collection = database.getCollection(collectionName,
                Objects.requireNonNull(ClassUtil.getClass(objs.stream().findAny().orElse(null)))).withCodecRegistry(pojoCodecRegistry);
        return collection.insertMany(objs);
    }

    /**
     * 更新一条记录(如果没有就插入)
     *
     * @param filter 过滤
     * @param update 更新
     * @return UpdateResult
     * @see Filters
     * @see com.mongodb.client.model.Updates
     */
    public UpdateResult updateOrCreate(String collectionName, Bson filter, Bson update) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName).withCodecRegistry(pojoCodecRegistry);
        return collection.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

    /**
     * 更新一条记录
     *
     * @param filter 过滤
     * @param update 更新
     * @return UpdateResult
     * @see Filters
     * @see com.mongodb.client.model.Updates
     */
    public UpdateResult updateOne(String collectionName, Bson filter, Bson update) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName).withCodecRegistry(pojoCodecRegistry);
        return collection.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

    /**
     * 更新多条记录
     *
     * @param filter 过滤
     * @param update 更新
     * @return UpdateResult
     * @see Filters
     * @see com.mongodb.client.model.Updates
     */
    public UpdateResult updateMany(String collectionName, Bson filter, Bson update) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName).withCodecRegistry(pojoCodecRegistry);
        return collection.updateMany(filter, update);
    }

    /**
     * 删除一条记录
     *
     * @param filter 过滤
     * @return DeleteResult
     * @see Filters
     */
    public DeleteResult deleteOne(String collectionName, Bson filter) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName).withCodecRegistry(pojoCodecRegistry);
        return collection.deleteOne(filter);
    }

    /**
     * 删除多条记录
     *
     * @param filter 过滤
     * @return DeleteResult
     * @see Filters
     */
    public DeleteResult deleteMany(String collectionName, Bson filter) {
        MongoDatabase database = mongoClient.getDatabase(dataBaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName).withCodecRegistry(pojoCodecRegistry);
        return collection.deleteMany(filter);
    }

}

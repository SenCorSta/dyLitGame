package com.sencorsta.ids;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.sencorsta.ids.core.database.mongo.MongoConfig;
import com.sencorsta.ids.core.database.mongo.MongoTemplate;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MongoTest {
    public static void main(String[] args) {
        MongoConfig mongoConfig = new MongoConfig();
        //username:password@
        String uri = "mongodb://admin:123456@192.168.2.142:27017";
        mongoConfig.setUrl(uri);
        mongoConfig.setDataBaseName("test_mongo");

        MongoTemplate mongoTemplate = new MongoTemplate(mongoConfig);

        mongoTemplate.deleteMany("test", Filters.exists("key"));

        HashMap<String, Integer> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("key", new Random().nextInt(1000));
        mongoTemplate.insertOne("test", objectObjectHashMap);
        mongoTemplate.insertOne("test", objectObjectHashMap);
        mongoTemplate.insertMany("test", Lists.newArrayList(objectObjectHashMap, objectObjectHashMap, objectObjectHashMap));

        Bson projectionFields = Projections.fields(
                Projections.include("title", "imdb"),
                Projections.excludeId());

        HashMap one = mongoTemplate.findOne("test", Filters.exists("key", true), HashMap.class);
        System.out.println(one);

        UpdateResult updateResult = mongoTemplate.updateMany("test", Filters.exists("key", true), Updates.combine(Updates.inc("key", 100)));
        System.out.println("updateResult = " + updateResult);

        List<HashMap> many = mongoTemplate.findAll("test", Filters.exists("key", true), HashMap.class);
        System.out.println(many);



        ForTestTable accountBean = new ForTestTable();
        accountBean.setAccount("123");
        accountBean.setPassword("123");
        accountBean.setPlayerId("123");
        mongoTemplate.insertOneTable(accountBean);
    }
}

package com.sencorsta.ids.core.database.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RedisReader {

    private final RedissonClient redissonClient;

    public RedisReader(String host, int port, String password, int database) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password).setDatabase(database);
        this.redissonClient = Redisson.create(config);
    }

    public String getValue(String key) {
        RBucket<byte[]> bucket = redissonClient.getBucket(key);
        byte[] compressedData = bucket.get();
        if (compressedData != null) {
            return decompress(compressedData);
        }
        return null;
    }

    public void setValue(String key, String value) {
        RBucket<byte[]> bucket = redissonClient.getBucket(key);
        byte[] compressedData = compress(value);
        bucket.set(compressedData);
    }

    public RLock getLock(String lockName) {
        return redissonClient.getLock(lockName);
    }

    private byte[] compress(String data) {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);

        try {
            byte[] compress = Snappy.compress(input);
            log.trace("压缩前:{} 压缩后:{}",input.length,compress.length);
            return compress;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String decompress(byte[] compressedData) {
        try {
            byte[] decompressedData = Snappy.uncompress(compressedData);
            log.trace("解压前:{} 解压后:{}",compressedData.length,decompressedData.length);
            return new String(decompressedData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void close() {
        redissonClient.shutdown();
    }
}

package com.kse.wmsv2.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RedisSampleController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @GetMapping("/redistest2")
    public void redisTest(){
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        // REDIS 保存
        hashOperations.put("testKey", "hashKey", "testValue");
        //　REDIS　出力
        Object redisRst = hashOperations.get("testKey", "hashKey");
        // REDIS 削除
        redisTemplate.delete("testKey");

    }
}

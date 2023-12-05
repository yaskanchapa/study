package com.kse.wmsv2.common.util;

import com.kse.wmsv2.oc_cs_003.dto.LoginUserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Redisと通信処理を行う共通部品クラス
 */
@Service
@Transactional
public class RedisUtil {

    /** Redis通信に使用するRedisTemplate */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Redisに保存処理を行う
     * @document
     * トークンをkeyとして使用して各権限の値をHash形式より保存する
     * @param key
     * @param hashKey
     * @param value
     * @return 正常:true 異常:false
     */
    public boolean saveRedis(String key, String hashKey, String value ){
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        // トークンをkeyとして使用する
        hashOperations.put(key, hashKey, value);
        return true;
    }

    public String loadRedis(String key, String hashKey){
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, hashKey).toString();
    }

    /**
     * Redisにkey一つの削除処理を行う
     * @return 正常:true 異常:false
     */
    public boolean delete(String key){
        redisTemplate.delete(key);
        return true;
    }


}

package com.dismai.handler;

import com.dismai.config.BloomFilterProperties;
import com.dismai.core.SpringUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.misc.MurmurHash3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloomFilterHandler {
    
    private final RBloomFilter<String> cachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final String filterKey;
    private final RScript script;
    
    public BloomFilterHandler(RedissonClient redissonClient, BloomFilterProperties bloomFilterProperties) {
        this.redissonClient = redissonClient;
        String name = SpringUtil.getPrefixDistinctionName() + "-" + bloomFilterProperties.getName();
        this.filterKey = name;
        this.script = redissonClient.getScript(StringCodec.INSTANCE);
        
        RBloomFilter<String> filter = redissonClient.getBloomFilter(name);
        filter.tryInit(bloomFilterProperties.getExpectedInsertions(),
                bloomFilterProperties.getFalseProbability());
        this.cachePenetrationBloomFilter = filter;
    }
    
    public boolean add(String data) {
        long size = cachePenetrationBloomFilter.getSize();
        int hashIterations = cachePenetrationBloomFilter.getHashIterations();
        
        long hash64 = MurmurHash3.hash64(data.getBytes());
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        
        List<Long> indices = new ArrayList<>();
        for (int i = 1; i <= hashIterations; i++) {
            long combinedHash = hash1 + (long) i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            indices.add(combinedHash % size);
        }
        
        String lua = "for i=1,#ARGV do\n" +
            "    redis.call('setbit', KEYS[1], ARGV[i], 1)\n" +
            "end\n" +
            "return 1";
        
        List<Object> keys = Collections.singletonList(filterKey);
        Object[] args = indices.toArray();
        
        script.eval(RScript.Mode.READ_WRITE, lua, RScript.ReturnType.VALUE, keys, args);
        return true;
    }
    
    public boolean contains(String data) {
        return cachePenetrationBloomFilter.contains(data);
    }
    
    public long getExpectedInsertions() {
        return cachePenetrationBloomFilter.getExpectedInsertions();
    }
    
    public double getFalseProbability() {
        return cachePenetrationBloomFilter.getFalseProbability();
    }
    
    public long getSize() {
        return cachePenetrationBloomFilter.getSize();
    }
    
    public int getHashIterations() {
        return cachePenetrationBloomFilter.getHashIterations();
    }
    
    public long count() {
        return cachePenetrationBloomFilter.count();
    }
}

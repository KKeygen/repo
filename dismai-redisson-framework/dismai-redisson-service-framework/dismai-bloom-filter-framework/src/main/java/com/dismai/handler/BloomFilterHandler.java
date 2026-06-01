package com.dismai.handler;

import com.dismai.config.BloomFilterProperties;
import com.dismai.core.SpringUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloomFilterHandler {
    
    private static final Logger log = LoggerFactory.getLogger(BloomFilterHandler.class);
    
    private final RBloomFilter<String> cachePenetrationBloomFilter;
    
    public BloomFilterHandler(RedissonClient redissonClient, BloomFilterProperties bloomFilterProperties){
        RBloomFilter<String> filter = redissonClient.getBloomFilter(
                SpringUtil.getPrefixDistinctionName() + "-" + bloomFilterProperties.getName());
        filter.tryInit(bloomFilterProperties.getExpectedInsertions(), 
                bloomFilterProperties.getFalseProbability());
        this.cachePenetrationBloomFilter = filter;
        log.info("BloomFilterHandler initialized, contains(test)={}", 
                filter.contains("__init_test__"));
    }
    
    public boolean add(String data) {
        log.info("BloomFilterHandler.add() called with data={}", data);
        try {
            boolean result = cachePenetrationBloomFilter.add(data);
            log.info("BloomFilterHandler.add() result={}, size={}, count={}", 
                    result, cachePenetrationBloomFilter.getSize(), cachePenetrationBloomFilter.count());
            boolean check = cachePenetrationBloomFilter.contains(data);
            log.info("BloomFilterHandler.add() immediate contains check={}", check);
            return result;
        } catch (Exception e) {
            log.error("BloomFilterHandler.add() exception", e);
            throw e;
        }
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

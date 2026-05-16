package com.dismai.config;

import com.dismai.constant.LockInfoType;
import com.dismai.handle.RedissonDataHandle;
import com.dismai.locallock.LocalLockCache;
import com.dismai.lockinfo.LockInfoHandle;
import com.dismai.lockinfo.factory.LockInfoHandleFactory;
import com.dismai.lockinfo.impl.RepeatExecuteLimitLockInfoHandle;
import com.dismai.repeatexecutelimit.aspect.RepeatExecuteLimitAspect;
import com.dismai.servicelock.factory.ServiceLockFactory;
import org.springframework.context.annotation.Bean;

public class RepeatExecuteLimitAutoConfiguration {
    
    @Bean(LockInfoType.REPEAT_EXECUTE_LIMIT)
    public LockInfoHandle repeatExecuteLimitHandle(){
        return new RepeatExecuteLimitLockInfoHandle();
    }
    
    @Bean
    public RepeatExecuteLimitAspect repeatExecuteLimitAspect(LocalLockCache localLockCache,
                                                             LockInfoHandleFactory lockInfoHandleFactory,
                                                             ServiceLockFactory serviceLockFactory,
                                                             RedissonDataHandle redissonDataHandle){
        return new RepeatExecuteLimitAspect(localLockCache, lockInfoHandleFactory,serviceLockFactory,redissonDataHandle);
    }
}
    
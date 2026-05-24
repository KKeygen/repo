package com.dismai.service.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.dismai.core.RepeatExecuteLimitConstants;
import com.dismai.dto.ProgramOrderCreateDto;
import com.dismai.enums.CompositeCheckType;
import com.dismai.enums.ProgramOrderVersion;
import com.dismai.initialize.impl.composite.CompositeContainer;
import com.dismai.repeatexecutelimit.annotion.RepeatExecuteLimit;
import com.dismai.service.ProgramOrderService;
import com.dismai.service.strategy.ProgramOrderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import static com.dismai.core.DistributedLockConstants.PROGRAM_ORDER_CREATE_V4;

@Slf4j
@Component
public class ProgramOrderV4Strategy implements ProgramOrderStrategy {
    
    @Autowired
    private ProgramOrderService programOrderService;
    
    @Autowired
    private com.dismai.locallock.LocalLockCache localLockCache;
    
    @Autowired
    private CompositeContainer compositeContainer;
    
    @RepeatExecuteLimit(
            name = RepeatExecuteLimitConstants.CREATE_PROGRAM_ORDER,
            keys = {"#programOrderCreateDto.userId","#programOrderCreateDto.programId"})
    @Override
    public String createOrder(ProgramOrderCreateDto programOrderCreateDto) {
        compositeContainer.execute(CompositeCheckType.PROGRAM_ORDER_CREATE_CHECK.getValue(),programOrderCreateDto);
        final int shardId = 0; // TODO: random shard after warmup aligned
        String lockKey = StrUtil.join("-", PROGRAM_ORDER_CREATE_V4,
                programOrderCreateDto.getProgramId(), programOrderCreateDto.getTicketCategoryId(), shardId);
        ReentrantLock lock = localLockCache.getLock(lockKey, false);
        lock.lock();
        try {
            return programOrderService.createNewAsync(programOrderCreateDto, shardId);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public String version() {
        return ProgramOrderVersion.V4_VERSION.getVersion();
    }
}

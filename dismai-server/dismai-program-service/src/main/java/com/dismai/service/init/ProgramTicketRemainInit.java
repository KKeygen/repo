package com.dismai.service.init;

import com.dismai.core.RedisKeyManage;
import com.dismai.redis.RedisKeyBuild;
import com.dismai.entity.TicketCategory;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dismai.mapper.TicketCategoryMapper;
import com.dismai.service.ProgramService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import com.dismai.initialize.base.AbstractApplicationPostConstructHandler;

@Component
public class ProgramTicketRemainInit extends AbstractApplicationPostConstructHandler {

    private static final Logger log = LoggerFactory.getLogger(ProgramTicketRemainInit.class);

    @Autowired
    private ProgramService programService;

    @Autowired
    private TicketCategoryMapper ticketCategoryMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Integer executeOrder() {
        return 5;
    }

    @Override
    public void executeInit(final ConfigurableApplicationContext context) {
        try {
            List<Long> programIds = programService.getAllProgramIdList();
            if (programIds == null || programIds.isEmpty()) {
                return;
            }
            int scanned = 0;
            int repaired = 0;
            int skipped = 0;
            int failed = 0;
            for (Long programId : programIds) {
                if (programId == null) {
                    continue;
                }
                List<TicketCategory> categories;
                try {
                    categories = ticketCategoryMapper.selectList(
                            Wrappers.lambdaQuery(TicketCategory.class)
                                    .eq(TicketCategory::getProgramId, programId));
                } catch (Exception e) {
                    log.error("ProgramTicketRemainInit: failed to load ticket categories for programId={}", programId, e);
                    failed++;
                    continue;
                }
                for (TicketCategory tc : categories) {
                    if (tc == null || tc.getId() == null) {
                        continue;
                    }
                    scanned++;
                    String totalKey = RedisKeyBuild.createRedisKey(
                            RedisKeyManage.PROGRAM_TICKET_TOTAL_REMAIN,
                            programId, tc.getId()).getRelKey();
                    try {
                        Object current = redissonClient.getBucket(totalKey).get();
                        if (isBroken(current)) {
                            redissonClient.getBucket(totalKey)
                                    .set(String.valueOf(tc.getRemainNumber()));
                            repaired++;
                            log.info("ProgramTicketRemainInit: repaired programId={} ticketCategoryId={} remain={}",
                                    programId, tc.getId(), tc.getRemainNumber());
                        } else {
                            skipped++;
                        }
                    } catch (Exception e) {
                        log.error("ProgramTicketRemainInit: failed to repair key={}", totalKey, e);
                        failed++;
                    }
                }
            }
            log.info("ProgramTicketRemainInit: scanned={} repaired={} skipped(valid)={} failed={}",
                    scanned, repaired, skipped, failed);
        } catch (Exception e) {
            log.error("ProgramTicketRemainInit: abort due to fatal error", e);
        }
    }

    private boolean isBroken(Object current) {
        if (current == null) {
            return true;
        }
        String s = current.toString().trim();
        if (s.isEmpty()) {
            return true;
        }
        try {
            long v = Long.parseLong(s);
            return v <= 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}

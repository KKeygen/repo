package com.dismai.service.init;

import com.dismai.BusinessThreadPool;
import com.dismai.initialize.base.AbstractApplicationPostConstructHandler;
import com.dismai.service.ProgramCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProgramCategoryInitData extends AbstractApplicationPostConstructHandler {
    
    @Autowired
    private ProgramCategoryService programCategoryService;
    
    
    @Override
    public Integer executeOrder() {
        return 1;
    }
    
    @Override
    public void executeInit(final ConfigurableApplicationContext context) {
        BusinessThreadPool.execute(() -> {
            programCategoryService.programCategoryRedisDataInit();
        });
    }
}

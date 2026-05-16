package com.dismai.service.init;

import cn.hutool.core.collection.CollectionUtil;
import com.dismai.handler.BloomFilterHandler;
import com.dismai.initialize.base.AbstractApplicationPostConstructHandler;
import com.dismai.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramBloomFilterInit extends AbstractApplicationPostConstructHandler {
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private BloomFilterHandler bloomFilterHandler;
    
    @Override
    public Integer executeOrder() {
        return 4;
    }
    
    @Override
    public void executeInit(final ConfigurableApplicationContext context) {
        List<Long> allProgramIdList = programService.getAllProgramIdList();
        if (CollectionUtil.isEmpty(allProgramIdList)) {
            return;
        }
        allProgramIdList.forEach(programId -> bloomFilterHandler.add(String.valueOf(programId)));
    }
}

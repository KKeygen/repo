package com.dismai.service.init;

import com.dismai.core.SpringUtil;
import com.dismai.initialize.base.AbstractApplicationPostConstructHandler;
import com.dismai.service.ProgramService;
import com.dismai.service.ProgramShowTimeService;
import com.dismai.util.BusinessEsHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProgramShowTimeRenewal extends AbstractApplicationPostConstructHandler {
    
    @Autowired
    private ProgramShowTimeService programShowTimeService;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private BusinessEsHandle businessEsHandle;
    
    @Override
    public Integer executeOrder() {
        return 2;
    }
    
    @Override
    public void executeInit(final ConfigurableApplicationContext context) {
        Set<Long> programIdSet = programShowTimeService.renewal();
        if (!programIdSet.isEmpty()) {
            boolean result = businessEsHandle.checkIndex(SpringUtil.getPrefixDistinctionName() + "-" +
                    ProgramDocumentParamName.INDEX_NAME, ProgramDocumentParamName.INDEX_TYPE);
            if (result) {
                businessEsHandle.deleteIndex(SpringUtil.getPrefixDistinctionName() + "-" +
                        ProgramDocumentParamName.INDEX_NAME);
            }
            for (Long programId : programIdSet) {
                programService.delRedisData(programId);
                programService.delLocalCache(programId);
            }
        }
    }
}

package com.dismai.service.strategy.impl;

import com.dismai.core.RepeatExecuteLimitConstants;
import com.dismai.dto.ProgramOrderCreateDto;
import com.dismai.enums.CompositeCheckType;
import com.dismai.enums.ProgramOrderVersion;
import com.dismai.initialize.impl.composite.CompositeContainer;
import com.dismai.repeatexecutelimit.annotion.RepeatExecuteLimit;
import com.dismai.service.ProgramOrderService;
import com.dismai.service.strategy.ProgramOrderStrategy;
import com.dismai.servicelock.annotion.ServiceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dismai.core.DistributedLockConstants.PROGRAM_ORDER_CREATE_V1;

@Component
public class ProgramOrderV1Strategy implements ProgramOrderStrategy {
    
    @Autowired
    private ProgramOrderService programOrderService;
    
    @Autowired
    private CompositeContainer compositeContainer;
    
    
    @RepeatExecuteLimit(
            name = RepeatExecuteLimitConstants.CREATE_PROGRAM_ORDER,
            keys = {"#programOrderCreateDto.userId","#programOrderCreateDto.programId"})
    @ServiceLock(name = PROGRAM_ORDER_CREATE_V1,keys = {"#programOrderCreateDto.programId"})
    @Override
    public String createOrder(final ProgramOrderCreateDto programOrderCreateDto) {
        compositeContainer.execute(CompositeCheckType.PROGRAM_ORDER_CREATE_CHECK.getValue(),programOrderCreateDto);
        return programOrderService.create(programOrderCreateDto);
    }
    
    @Override
    public String version() {
        return ProgramOrderVersion.V1_VERSION.getVersion();
    }
}

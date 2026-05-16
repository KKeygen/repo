package com.dismai.service.strategy.impl;

import com.dismai.core.RepeatExecuteLimitConstants;
import com.dismai.dto.ProgramOrderCreateDto;
import com.dismai.enums.CompositeCheckType;
import com.dismai.enums.ProgramOrderVersion;
import com.dismai.initialize.impl.composite.CompositeContainer;
import com.dismai.repeatexecutelimit.annotion.RepeatExecuteLimit;
import com.dismai.service.ProgramOrderService;
import com.dismai.service.strategy.BaseProgramOrder;
import com.dismai.service.strategy.ProgramOrderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dismai.core.DistributedLockConstants.PROGRAM_ORDER_CREATE_V3;

@Slf4j
@Component
public class ProgramOrderV3Strategy implements ProgramOrderStrategy {
    
    @Autowired
    private ProgramOrderService programOrderService;
    
    @Autowired
    private BaseProgramOrder baseProgramOrder;
    
    @Autowired
    private CompositeContainer compositeContainer;
    
    @RepeatExecuteLimit(
            name = RepeatExecuteLimitConstants.CREATE_PROGRAM_ORDER,
            keys = {"#programOrderCreateDto.userId","#programOrderCreateDto.programId"})
    @Override
    public String createOrder(ProgramOrderCreateDto programOrderCreateDto) {
        compositeContainer.execute(CompositeCheckType.PROGRAM_ORDER_CREATE_CHECK.getValue(),programOrderCreateDto);
        return baseProgramOrder.localLockCreateOrder(PROGRAM_ORDER_CREATE_V3,programOrderCreateDto,
                () -> programOrderService.createNew(programOrderCreateDto));
    }
    
    @Override
    public String version() {
        return ProgramOrderVersion.V3_VERSION.getVersion();
    }
}

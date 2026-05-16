package com.dismai.service.composite;

import com.dismai.dto.ProgramOrderCreateDto;
import com.dismai.enums.CompositeCheckType;
import com.dismai.initialize.impl.composite.AbstractComposite;

public abstract class AbstractProgramCheckHandler extends AbstractComposite<ProgramOrderCreateDto> {
    
    @Override
    public String type() {
        return CompositeCheckType.PROGRAM_ORDER_CREATE_CHECK.getValue();
    }
}

package com.dismai.service.composite;

import com.dismai.dto.ProgramGetDto;
import com.dismai.enums.BaseCode;
import com.dismai.enums.CompositeCheckType;
import com.dismai.exception.DismaiFrameException;
import com.dismai.handler.BloomFilterHandler;
import com.dismai.initialize.impl.composite.AbstractComposite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramBloomFilterCheckHandler extends AbstractComposite<ProgramGetDto> {
    
    @Autowired
    private BloomFilterHandler bloomFilterHandler;
    
    @Override
    protected void execute(final ProgramGetDto param) {
        boolean contains = bloomFilterHandler.contains(String.valueOf(param.getId()));
        if (!contains) {
            throw new DismaiFrameException(BaseCode.PROGRAM_NOT_EXIST);
        }
    }
    
    @Override
    public String type() {
        return CompositeCheckType.PROGRAM_DETAIL_CHECK.getValue();
    }
    
    @Override
    public Integer executeParentOrder() {
        return 0;
    }
    
    @Override
    public Integer executeTier() {
        return 1;
    }
    
    @Override
    public Integer executeOrder() {
        return 1;
    }
}

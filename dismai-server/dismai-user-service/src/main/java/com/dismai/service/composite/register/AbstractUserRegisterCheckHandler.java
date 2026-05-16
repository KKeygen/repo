package com.dismai.service.composite.register;

import com.dismai.dto.UserRegisterDto;
import com.dismai.enums.CompositeCheckType;
import com.dismai.initialize.impl.composite.AbstractComposite;

public abstract class AbstractUserRegisterCheckHandler extends AbstractComposite<UserRegisterDto> {
    
    @Override
    public String type() {
        return CompositeCheckType.USER_REGISTER_CHECK.getValue();
    }
}

package com.dismai.initialize.base;

import org.springframework.beans.factory.InitializingBean;

import static com.dismai.initialize.constant.InitializeHandlerType.APPLICATION_EVENT_LISTENER;

public abstract class AbstractApplicationStartEventListenerHandler implements InitializeHandler {
    
    @Override
    public String type() {
        return APPLICATION_EVENT_LISTENER;
    }
}

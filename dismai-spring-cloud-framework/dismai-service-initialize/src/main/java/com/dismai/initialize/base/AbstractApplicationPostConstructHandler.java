package com.dismai.initialize.base;

import jakarta.annotation.PostConstruct;

import static com.dismai.initialize.constant.InitializeHandlerType.APPLICATION_POST_CONSTRUCT;

public abstract class AbstractApplicationPostConstructHandler implements InitializeHandler {
    
    @Override
    public String type() {
        return APPLICATION_POST_CONSTRUCT;
    }
}

package com.dismai.initialize.execute;

import com.dismai.initialize.execute.base.AbstractApplicationExecute;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ConfigurableApplicationContext;

import static com.dismai.initialize.constant.InitializeHandlerType.APPLICATION_INITIALIZING_BEAN;

public class ApplicationInitializingBeanExecute extends AbstractApplicationExecute implements InitializingBean {
    
    public ApplicationInitializingBeanExecute(ConfigurableApplicationContext applicationContext){
        super(applicationContext);
    }
    
    @Override
    public void afterPropertiesSet() {
        execute();
    }
    
    @Override
    public String type() {
        return APPLICATION_INITIALIZING_BEAN;
    }
}

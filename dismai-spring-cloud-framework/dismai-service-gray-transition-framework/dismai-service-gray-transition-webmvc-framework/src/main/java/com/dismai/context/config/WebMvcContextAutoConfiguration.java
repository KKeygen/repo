package com.dismai.context.config;

import com.dismai.context.ContextHandler;
import com.dismai.context.impl.WebMvcContextHandler;
import org.springframework.context.annotation.Bean;

public class WebMvcContextAutoConfiguration {
    
    @Bean
    public ContextHandler webMvcContext(){
        return new WebMvcContextHandler();
    }
}

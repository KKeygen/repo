package com.dismai.balance.config;

import com.dismai.context.ContextHandler;
import com.dismai.enhance.config.EnhanceLoadBalancerClientConfiguration;
import com.dismai.enhance.config.EnhanceLoadBalancerClientConfiguration.BlockingSupportConfiguration;
import com.dismai.enhance.config.EnhanceLoadBalancerClientConfiguration.ReactiveSupportConfiguration;
import com.dismai.filter.AbstractServerFilter;
import com.dismai.filter.impl.ServerGrayFilter;
import com.dismai.fiterbalance.DefaultFilterLoadBalance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;

import java.util.List;

@LoadBalancerClients(defaultConfiguration = {EnhanceLoadBalancerClientConfiguration.class, ReactiveSupportConfiguration.class, BlockingSupportConfiguration.class})
public class GrayLoadBalanceAutoConfiguration {
    
    @Bean
    public DefaultFilterLoadBalance defaultFilterLoadBalance(List<AbstractServerFilter> strategyEnabledFilterList){
        return new DefaultFilterLoadBalance(strategyEnabledFilterList);
    }
    
    @Bean
    public AbstractServerFilter serverGrayFilter(ContextHandler contextHandler) {
        return new ServerGrayFilter(contextHandler);
    }
}

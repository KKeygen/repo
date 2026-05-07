package com.dismai.pay;

import com.dismai.enums.BaseCode;
import com.dismai.exception.DismaiFrameException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PayStrategyContext {
    
    private final Map<String,PayStrategyHandler> payStrategyHandlerMap = new HashMap<>();
    
    public void put(String channel,PayStrategyHandler payStrategyHandler){
        payStrategyHandlerMap.put(channel,payStrategyHandler);
    }
    
    public PayStrategyHandler get(String channel){
        return Optional.ofNullable(payStrategyHandlerMap.get(channel)).orElseThrow(
                () -> new DismaiFrameException(BaseCode.PAY_STRATEGY_NOT_EXIST));
    }
}

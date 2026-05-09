package com.dismai.service.strategy;

import com.dismai.enums.BaseCode;
import com.dismai.exception.DismaiFrameException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ProgramOrderContext {
    
    /**
     **/
    private static final Map<String,ProgramOrderStrategy> MAP = new HashMap<>(8);
    
    @Autowired
    private List<ProgramOrderStrategy> programOrderStrategyList;
    
    @PostConstruct
    public void init() {
        for (ProgramOrderStrategy programOrderStrategy : programOrderStrategyList) {
            MAP.put(programOrderStrategy.version(), programOrderStrategy);
        }
    }
    
    public ProgramOrderStrategy get(String version){
        return Optional.ofNullable(MAP.get(version)).orElseThrow(() -> 
                new DismaiFrameException(BaseCode.PROGRAM_ORDER_STRATEGY_NOT_EXIST));
    }
}

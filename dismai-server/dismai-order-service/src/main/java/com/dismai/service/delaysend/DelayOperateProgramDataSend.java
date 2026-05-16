package com.dismai.service.delaysend;

import com.dismai.context.DelayQueueContext;
import com.dismai.core.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dismai.service.constant.OrderConstant.DELAY_OPERATE_PROGRAM_DATA_TIME;
import static com.dismai.service.constant.OrderConstant.DELAY_OPERATE_PROGRAM_DATA_TIME_UNIT;
import static com.dismai.service.constant.OrderConstant.DELAY_OPERATE_PROGRAM_DATA_TOPIC;

@Slf4j
@Component
public class DelayOperateProgramDataSend {
    
    @Autowired
    private DelayQueueContext delayQueueContext;
    
    public void sendMessage(String message){
        try {
            delayQueueContext.sendMessage(SpringUtil.getPrefixDistinctionName() + "-" + DELAY_OPERATE_PROGRAM_DATA_TOPIC,
                    message, DELAY_OPERATE_PROGRAM_DATA_TIME, DELAY_OPERATE_PROGRAM_DATA_TIME_UNIT);
        }catch (Exception e) {
            log.error("send message error message : {}",message,e);
        }
        
    }
}

package com.dismai.service.delayconsumer;

import com.alibaba.fastjson.JSON;
import com.dismai.core.ConsumerTask;
import com.dismai.core.SpringUtil;
import com.dismai.util.StringUtil;
import com.dismai.dto.ProgramOperateDataDto;
import com.dismai.service.ProgramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dismai.service.constant.ProgramOrderConstant.DELAY_OPERATE_PROGRAM_DATA_TOPIC;

@Slf4j
@Component
public class DelayOperateProgramDataConsumer implements ConsumerTask {
    
    @Autowired
    private ProgramService programService;
    
    @Override
    public void execute(String content) {
        log.info("延迟操作节目数据消息进行消费 content : {}", content);
        if (StringUtil.isEmpty(content)) {
            log.error("延迟队列消息不存在");
            return;
        }
        ProgramOperateDataDto programOperateDataDto = JSON.parseObject(content, ProgramOperateDataDto.class);
        programService.operateProgramData(programOperateDataDto);
    }
    
    @Override
    public String topic() {
        return SpringUtil.getPrefixDistinctionName() + "-" + DELAY_OPERATE_PROGRAM_DATA_TOPIC;
    }
}

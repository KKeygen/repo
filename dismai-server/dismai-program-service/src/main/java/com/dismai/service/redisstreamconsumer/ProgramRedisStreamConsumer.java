package com.dismai.service.redisstreamconsumer;

import com.dismai.MessageConsumer;
import com.dismai.service.ProgramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.stereotype.Component;

    
@Slf4j
@Component
public class ProgramRedisStreamConsumer implements MessageConsumer {
    
    @Autowired
    private ProgramService programService;
    
    @Override
    public void accept(ObjectRecord<String, String> message) {
        Long programId = Long.parseLong(message.getValue());
        programService.delLocalCache(programId);
    }
}

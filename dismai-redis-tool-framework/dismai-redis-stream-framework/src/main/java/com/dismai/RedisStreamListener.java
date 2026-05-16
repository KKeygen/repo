package com.dismai;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@AllArgsConstructor
public class RedisStreamListener implements org.springframework.data.redis.stream.StreamListener<String, ObjectRecord<String, String>> {
    
    private final MessageConsumer messageConsumer;
    private final StringRedisTemplate stringRedisTemplate;
    private final String consumerGroup;
    

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        RecordId messageId = message.getId();
        String streamName = message.getStream();
        String value = message.getValue();
        try{
            log.info("redis stream 消费到了数据 messageId : {}, streamName : {}, message : {}", 
                    messageId, streamName, value);
            messageConsumer.accept(message);
            stringRedisTemplate.opsForStream().acknowledge(consumerGroup, streamName, messageId);
        }catch (Exception e){
            log.error("onMessage error messageId : {}, streamName : {}", messageId, streamName, e);
        }
    }
}

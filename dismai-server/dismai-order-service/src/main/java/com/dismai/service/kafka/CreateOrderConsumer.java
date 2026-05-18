package com.dismai.service.kafka;

import com.alibaba.fastjson.JSON;
import com.dismai.dto.OrderCreateDto;
import com.dismai.dto.OrderTicketUserCreateDto;
import com.dismai.enums.OrderStatus;
import com.dismai.service.OrderService;
import com.dismai.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dismai.constant.Constant.SPRING_INJECT_PREFIX_DISTINCTION_NAME;

@Slf4j
@AllArgsConstructor
@Component
public class CreateOrderConsumer {

    @Autowired
    private OrderService orderService;

    public static Long MESSAGE_DELAY_TIME = 5000L;

    /**
     **/
    @KafkaListener(topics = {SPRING_INJECT_PREFIX_DISTINCTION_NAME+"-"+"${spring.kafka.topic:create_order}"})
    public void consumerOrderMessage(ConsumerRecord<String,String> consumerRecord, Acknowledgment acknowledgment){
        try {
            String value = Optional.ofNullable(consumerRecord.value()).map(String::valueOf).orElse(null);
            if (StringUtil.isEmpty(value)) {
                log.warn("消费到kafka的创建订单消息为空 topic : {} partition : {} offset : {}",
                        consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset());
                acknowledgment.acknowledge();
                return;
            }
            OrderCreateDto orderCreateDto = JSON.parseObject(value, OrderCreateDto.class);

            long createOrderTimeTimestamp = orderCreateDto.getCreateOrderTime().getTime();

            long currentTimeTimestamp = System.currentTimeMillis();

            long delayTime = currentTimeTimestamp - createOrderTimeTimestamp;

            log.info("消费到kafka的创建订单消息 消息体: {} 延迟时间 : {} 毫秒",value,delayTime);

            if (currentTimeTimestamp - createOrderTimeTimestamp > MESSAGE_DELAY_TIME) {
                log.info("消费到kafka的创建订单消息延迟时间大于了 {} 毫秒 此订单消息被丢弃 订单号 : {}",
                        MESSAGE_DELAY_TIME,orderCreateDto.getOrderNumber());
                Map<Long, List<OrderTicketUserCreateDto>> orderTicketUserSeatList =
                        orderCreateDto.getOrderTicketUserCreateDtoList().stream().collect(Collectors.groupingBy(OrderTicketUserCreateDto::getTicketCategoryId));
                Map<Long,List<Long>> seatMap = new HashMap<>(orderTicketUserSeatList.size());
                orderTicketUserSeatList.forEach((k,v) -> {
                    seatMap.put(k,v.stream().map(OrderTicketUserCreateDto::getSeatId).collect(Collectors.toList()));
                });
                List<String> idNumberList = orderCreateDto.getOrderTicketUserCreateDtoList().stream()
                        .map(OrderTicketUserCreateDto::getIdNumber)
                        .filter(StringUtil::isNotEmpty)
                        .distinct()
                        .collect(Collectors.toList());
                orderService.updateProgramRelatedDataMq(orderCreateDto.getProgramId(),seatMap, OrderStatus.CANCEL,idNumberList);
            }else {
                String orderNumber = orderService.createMq(orderCreateDto);
                log.info("消费到kafka的创建订单消息 创建订单成功 订单号 : {}",orderNumber);
            }
            acknowledgment.acknowledge();
        }catch (Exception e) {
            log.error("处理消费到kafka的创建订单消息失败 error",e);
            throw new RuntimeException("Kafka order message processing failed, will retry", e);
        }
    }
}

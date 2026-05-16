package com.dismai.scheduletask;

import com.dismai.BusinessThreadPool;
import com.dismai.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderDataTask {
    
    @Autowired
    private OrderService orderService;
    
    @Scheduled(cron = "0 0 23 * * ?")
    public void executeTask(){
        BusinessThreadPool.execute( () -> {
            try {
                log.warn("订单服务定时任务重置执行");
                // FIXME: 当前实现会删除所有有效订单(order_status=1,2,3,4)，导致每日数据丢失
                // 需要改为只删除已标记删除或超过保留期的订单
                // orderService.delOrderAndOrderTicketUser();
            }catch (Exception e) {
                log.error("executeTask error",e);
            }
        });
    }
}

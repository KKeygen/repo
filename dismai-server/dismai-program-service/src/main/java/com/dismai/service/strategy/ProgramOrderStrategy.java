package com.dismai.service.strategy;

import com.dismai.dto.ProgramOrderCreateDto;

public interface ProgramOrderStrategy {
    
    /**
     * 创建订单
     * @param programOrderCreateDto 订单参数
     * @return 订单编号
     * */
    String createOrder(ProgramOrderCreateDto programOrderCreateDto);
    
    /**
     * 获取版本号
     * @return 版本号
     * */
    String version();
}

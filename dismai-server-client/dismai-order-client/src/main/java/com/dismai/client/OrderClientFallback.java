package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.AccountOrderCountDto;
import com.dismai.dto.OrderCreateDto;
import com.dismai.enums.BaseCode;
import com.dismai.vo.AccountOrderCountVo;
import org.springframework.stereotype.Component;

@Component
public class OrderClientFallback implements OrderClient {
    
    @Override
    public ApiResponse<String> create(final OrderCreateDto orderCreateDto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<AccountOrderCountVo> accountOrderCount(final AccountOrderCountDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
}

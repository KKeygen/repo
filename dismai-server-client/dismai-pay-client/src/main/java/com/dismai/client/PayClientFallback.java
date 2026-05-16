package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.NotifyDto;
import com.dismai.dto.PayDto;
import com.dismai.dto.RefundDto;
import com.dismai.dto.TradeCheckDto;
import com.dismai.enums.BaseCode;
import com.dismai.vo.NotifyVo;
import com.dismai.vo.TradeCheckVo;
import org.springframework.stereotype.Component;

@Component
public class PayClientFallback implements PayClient{
    
    @Override
    public ApiResponse<String> commonPay(final PayDto payDto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<NotifyVo> notify(final NotifyDto notifyDto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<TradeCheckVo> tradeCheck(final TradeCheckDto tradeCheckDto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<String> refund(final RefundDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
}

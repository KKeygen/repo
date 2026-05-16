package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.NotifyDto;
import com.dismai.dto.PayDto;
import com.dismai.dto.RefundDto;
import com.dismai.dto.TradeCheckDto;
import com.dismai.vo.NotifyVo;
import com.dismai.vo.TradeCheckVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import static com.dismai.constant.Constant.SPRING_INJECT_PREFIX_DISTINCTION_NAME;

@Component
@FeignClient(value = SPRING_INJECT_PREFIX_DISTINCTION_NAME+"-"+"pay-service",fallback = PayClientFallback.class)
public interface PayClient {
    /**
     * 支付
     * @param dto 参数
     * @return 结果
     * */
    @PostMapping(value = "/pay/common/pay")
    ApiResponse<String> commonPay(PayDto dto);
    /**
     * 回调
     * @param dto 参数
     * @return 结果
     * */
    @PostMapping(value = "/pay/notify")
    ApiResponse<NotifyVo> notify(NotifyDto dto);
    /**
     * 查询支付状态
     * @param dto 参数
     * @return 结果
     * */
    @PostMapping(value = "/pay/trade/check")
    ApiResponse<TradeCheckVo> tradeCheck(TradeCheckDto dto);
    /**
     * 退款
     * @param dto 参数
     * @return 结果
     * */
    @PostMapping(value = "/pay/refund")
    ApiResponse<String> refund(RefundDto dto);
}

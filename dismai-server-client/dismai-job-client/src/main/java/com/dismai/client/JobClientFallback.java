package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.JobCallBackDto;
import com.dismai.enums.BaseCode;
import org.springframework.stereotype.Component;

@Component
public class JobClientFallback implements JobClient {
    
    @Override
    public ApiResponse<Boolean> callBack(final JobCallBackDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
}

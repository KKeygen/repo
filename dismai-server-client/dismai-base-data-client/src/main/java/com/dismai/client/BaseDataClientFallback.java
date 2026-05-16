package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.AreaGetDto;
import com.dismai.dto.AreaSelectDto;
import com.dismai.dto.GetChannelDataByCodeDto;
import com.dismai.enums.BaseCode;
import com.dismai.vo.AreaVo;
import com.dismai.vo.GetChannelDataVo;
import com.dismai.vo.TokenDataVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaseDataClientFallback implements BaseDataClient{
    @Override
    public ApiResponse<GetChannelDataVo> getByCode(final GetChannelDataByCodeDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<TokenDataVo> get() {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<List<AreaVo>> selectByIdList(final AreaSelectDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<AreaVo> getById(final AreaGetDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
}

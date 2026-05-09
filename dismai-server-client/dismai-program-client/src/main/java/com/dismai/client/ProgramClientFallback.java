package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.TicketCategoryListByProgramDto;
import com.dismai.enums.BaseCode;
import com.dismai.vo.TicketCategoryDetailVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramClientFallback implements ProgramClient {
    
    @Override
    public ApiResponse<List<TicketCategoryDetailVo>> selectListByProgram(TicketCategoryListByProgramDto ticketCategoryListByProgramDto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
}

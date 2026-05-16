package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.TicketUserListDto;
import com.dismai.dto.UserGetAndTicketUserListDto;
import com.dismai.dto.UserIdDto;
import com.dismai.enums.BaseCode;
import com.dismai.vo.UserGetAndTicketUserListVo;
import com.dismai.vo.TicketUserVo;
import com.dismai.vo.UserVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserClientFallback implements UserClient {
    
    @Override
    public ApiResponse<UserVo> getById(final UserIdDto userIdDto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<List<TicketUserVo>> list(final TicketUserListDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
    
    @Override
    public ApiResponse<UserGetAndTicketUserListVo> getUserAndTicketUserList(final UserGetAndTicketUserListDto dto) {
        return ApiResponse.error(BaseCode.SYSTEM_ERROR);
    }
}

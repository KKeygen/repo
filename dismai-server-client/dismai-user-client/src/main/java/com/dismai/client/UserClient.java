package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.TicketUserListDto;
import com.dismai.dto.UserGetAndTicketUserListDto;
import com.dismai.dto.UserIdDto;
import com.dismai.vo.TicketUserVo;
import com.dismai.vo.UserGetAndTicketUserListVo;
import com.dismai.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static com.dismai.constant.Constant.SPRING_INJECT_PREFIX_DISTINCTION_NAME;

@Component
@FeignClient(value = SPRING_INJECT_PREFIX_DISTINCTION_NAME+"-"+"user-service",fallback = UserClientFallback.class)
public interface UserClient {
    
    /**
     * 查询用户(通过id)
     * @param dto 参数
     * @return 结果
     * */
    @PostMapping(value = "/user/getById")
    ApiResponse<UserVo> getById(UserIdDto dto);
    

    /**
     * 查询购票人(通过userId)
     * @param dto 参数
     * @return 结果
     * */
    @PostMapping(value = "/ticket/user/list")
    ApiResponse<List<TicketUserVo>> list(TicketUserListDto dto);
    
    /**
     * 查询用户和购票人集合
     * @param dto 参数
     * @return 结果
     */
    @PostMapping(value = "/user/get/user/ticket/list")
    ApiResponse<UserGetAndTicketUserListVo> getUserAndTicketUserList(UserGetAndTicketUserListDto dto);
    
}

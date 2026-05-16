package com.dismai.client;

import com.dismai.common.ApiResponse;
import com.dismai.dto.TicketCategoryListByProgramDto;
import com.dismai.vo.TicketCategoryDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static com.dismai.constant.Constant.SPRING_INJECT_PREFIX_DISTINCTION_NAME;

@Component
@FeignClient(value = SPRING_INJECT_PREFIX_DISTINCTION_NAME+"-"+"program-service",fallback = ProgramClientFallback.class)
public interface ProgramClient {

    /**
     * 查询票档集合(通过节目查询)
     * @param ticketCategoryListByProgramDto 参数
     * @return 结果
     * */
    @PostMapping(value = "/ticket/category/select/list/by/program")
    ApiResponse<List<TicketCategoryDetailVo>> selectListByProgram(TicketCategoryListByProgramDto ticketCategoryListByProgramDto);
}

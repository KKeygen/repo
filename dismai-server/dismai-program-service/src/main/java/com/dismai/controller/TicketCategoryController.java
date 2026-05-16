package com.dismai.controller;

import com.dismai.common.ApiResponse;
import com.dismai.dto.TicketCategoryAddDto;
import com.dismai.dto.TicketCategoryDto;
import com.dismai.dto.TicketCategoryListByProgramDto;
import com.dismai.service.TicketCategoryService;
import com.dismai.vo.TicketCategoryDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticket/category")
@Tag(name = "ticket-category", description = "票档")
public class TicketCategoryController {
    
    @Autowired
    private TicketCategoryService ticketCategoryService;
    
    
    @Operation(summary  = "添加")
    @PostMapping(value = "/add")
    public ApiResponse<Long> add(@Valid @RequestBody TicketCategoryAddDto ticketCategoryAddDto) {
        return ApiResponse.ok(ticketCategoryService.add(ticketCategoryAddDto));
    }
    
    @Operation(summary  = "查询详情")
    @PostMapping(value = "/detail")
    public ApiResponse<TicketCategoryDetailVo> detail(@Valid @RequestBody TicketCategoryDto ticketCategoryDto) {
        return ApiResponse.ok(ticketCategoryService.detail(ticketCategoryDto));
    }
    
    @Operation(summary  = "查询集合")
    @PostMapping(value = "/select/list/by/program")
    public ApiResponse<List<TicketCategoryDetailVo>> selectListByProgram(@Valid @RequestBody TicketCategoryListByProgramDto ticketCategoryListByProgramDto) {
        return ApiResponse.ok(ticketCategoryService.selectListByProgram(ticketCategoryListByProgramDto));
    }
}

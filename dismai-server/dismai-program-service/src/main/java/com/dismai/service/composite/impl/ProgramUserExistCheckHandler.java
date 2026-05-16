package com.dismai.service.composite.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.dismai.client.OrderClient;
import com.dismai.client.UserClient;
import com.dismai.common.ApiResponse;
import com.dismai.core.RedisKeyManage;
import com.dismai.dto.AccountOrderCountDto;
import com.dismai.dto.ProgramGetDto;
import com.dismai.dto.ProgramOrderCreateDto;
import com.dismai.dto.TicketUserListDto;
import com.dismai.enums.BaseCode;
import com.dismai.exception.DismaiFrameException;
import com.dismai.redis.RedisCache;
import com.dismai.redis.RedisKeyBuild;
import com.dismai.service.ProgramService;
import com.dismai.service.composite.AbstractProgramCheckHandler;
import com.dismai.service.tool.TokenExpireManager;
import com.dismai.vo.AccountOrderCountVo;
import com.dismai.vo.ProgramVo;
import com.dismai.vo.TicketUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProgramUserExistCheckHandler extends AbstractProgramCheckHandler {
    
    @Autowired
    private UserClient userClient;
    
    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private OrderClient orderClient;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private TokenExpireManager tokenExpireManager;
    
    @Override
    protected void execute(ProgramOrderCreateDto programOrderCreateDto) {
        List<TicketUserVo> ticketUserVoList = redisCache.getValueIsList(RedisKeyBuild.createRedisKey(
                RedisKeyManage.TICKET_USER_LIST, programOrderCreateDto.getUserId()), TicketUserVo.class);
        if (CollectionUtil.isEmpty(ticketUserVoList)) {
            TicketUserListDto ticketUserListDto = new TicketUserListDto();
            ticketUserListDto.setUserId(programOrderCreateDto.getUserId());
            ApiResponse<List<TicketUserVo>> apiResponse = userClient.list(ticketUserListDto);
            if (Objects.equals(apiResponse.getCode(), BaseCode.SUCCESS.getCode())) {
                ticketUserVoList = apiResponse.getData();
            }else {
                log.error("user client rpc getUserAndTicketUserList select response : {}", JSON.toJSONString(apiResponse));
                throw new DismaiFrameException(apiResponse);
            }
        }
        if (CollectionUtil.isEmpty(ticketUserVoList)) {
            throw new DismaiFrameException(BaseCode.TICKET_USER_EMPTY);
        }
        Map<Long, TicketUserVo> ticketUserVoMap = ticketUserVoList.stream()
                .collect(Collectors.toMap(TicketUserVo::getId, ticketUserVo -> ticketUserVo, (v1, v2) -> v2));
        List<String> idNumberList = new java.util.ArrayList<>();
        for (Long ticketUserId : programOrderCreateDto.getTicketUserIdList()) {
            TicketUserVo tu = ticketUserVoMap.get(ticketUserId);
            if (Objects.isNull(tu)) {
                throw new DismaiFrameException(BaseCode.TICKET_USER_EMPTY);
            }
            String idNumber = tu.getIdNumber();
            RedisKeyBuild riskKey = RedisKeyBuild.createRedisKey(RedisKeyManage.ID_CARD_BRUSH_RISK, idNumber);
            Long reqCount = redisCache.incrBy(riskKey, 1);
            if (reqCount == 1) {
                redisCache.expire(riskKey, 10, java.util.concurrent.TimeUnit.SECONDS);
            }
            if (reqCount > 5) {
                log.warn("风控警告: 身份证 {} 触发高频刷票行为！", idNumber);
                throw new DismaiFrameException(BaseCode.SYSTEM_ERROR); // 使用 SYSTEM_ERROR 或其他自定义code
            }
            idNumberList.add(idNumber);
        }
        programOrderCreateDto.setIdNumberList(idNumberList);
        ProgramGetDto programGetDto = new ProgramGetDto();
        programGetDto.setId(programOrderCreateDto.getProgramId());
        ProgramVo programVo = programService.detailV2(programGetDto);
        if (Objects.isNull(programVo)) {
            throw new DismaiFrameException(BaseCode.PROGRAM_NOT_EXIST);
        }
        Integer count = 0;
        if (redisCache.hasKey(RedisKeyBuild.createRedisKey(RedisKeyManage.ACCOUNT_ORDER_COUNT,
                programOrderCreateDto.getUserId(),programOrderCreateDto.getProgramId()))) {
            count = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.ACCOUNT_ORDER_COUNT,
                    programOrderCreateDto.getUserId(),programOrderCreateDto.getProgramId()), Integer.class);
        }else {
            AccountOrderCountDto accountOrderCountDto = new AccountOrderCountDto();
            accountOrderCountDto.setUserId(programOrderCreateDto.getUserId());
            accountOrderCountDto.setProgramId(programOrderCreateDto.getProgramId());
            ApiResponse<AccountOrderCountVo> apiResponse = orderClient.accountOrderCount(accountOrderCountDto);
            if (Objects.equals(apiResponse.getCode(), BaseCode.SUCCESS.getCode())) {
                count = Optional.ofNullable(apiResponse.getData()).map(AccountOrderCountVo::getCount).orElse(0);
                redisCache.set(RedisKeyBuild.createRedisKey(RedisKeyManage.ACCOUNT_ORDER_COUNT,
                                programOrderCreateDto.getUserId(),
                                programOrderCreateDto.getProgramId()),
                        count, tokenExpireManager.getTokenExpireTime() + 1, TimeUnit.MINUTES);
            }
        }
        
        Integer seatCount = Optional.ofNullable(programOrderCreateDto.getSeatDtoList()).map(List::size).orElse(0);
        
        Integer ticketCount = Optional.ofNullable(programOrderCreateDto.getTicketCount()).orElse(0);
        if (seatCount != 0) {
            count = count + seatCount;
        }else if (ticketCount != 0) {
            count = count + ticketCount;
        }
//        if (count > programVo.getPerAccountLimitPurchaseCount()) {
//            throw new DismaiFrameException(BaseCode.PER_ACCOUNT_PURCHASE_COUNT_OVER_LIMIT);
//        }
    }
    
    @Override
    public Integer executeParentOrder() {
        return 1;
    }
    
    @Override
    public Integer executeTier() {
        return 2;
    }
    
    @Override
    public Integer executeOrder() {
        return 2;
    }
}

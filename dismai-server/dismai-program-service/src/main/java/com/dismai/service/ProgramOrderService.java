package com.dismai.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.fsg.uid.UidGenerator;
import com.dismai.client.OrderClient;
import com.dismai.common.ApiResponse;
import com.dismai.core.RedisKeyManage;
import com.dismai.dto.DelayOrderCancelDto;
import com.dismai.dto.OrderCreateDto;
import com.dismai.dto.OrderTicketUserCreateDto;
import com.dismai.dto.ProgramOrderCreateDto;
import com.dismai.dto.SeatDto;
import com.dismai.entity.ProgramShowTime;
import com.dismai.enums.BaseCode;
import com.dismai.enums.OrderStatus;
import com.dismai.enums.SellStatus;
import com.dismai.exception.DismaiFrameException;
import com.dismai.redis.RedisKeyBuild;
import com.dismai.service.delaysend.DelayOrderCancelSend;
import com.dismai.service.kafka.CreateOrderMqDomain;
import com.dismai.service.kafka.CreateOrderSend;
import com.dismai.service.lua.ProgramCacheCreateOrderData;
import com.dismai.service.lua.ProgramCacheCreateOrderResolutionOperate;
import com.dismai.service.lua.ProgramCacheResolutionOperate;
import com.dismai.service.tool.SeatMatch;
import com.dismai.util.DateUtils;
import com.dismai.vo.ProgramVo;
import com.dismai.vo.SeatVo;
import com.dismai.vo.TicketCategoryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.dismai.redis.RedisCache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.dismai.redis.RedisCache;

import static com.dismai.service.constant.ProgramOrderConstant.ORDER_TABLE_COUNT;

@Slf4j
@Service
public class ProgramOrderService {
    
    @Autowired
    private OrderClient orderClient;
    
    @Autowired
    private UidGenerator uidGenerator;
    
    @Autowired
    private ProgramCacheResolutionOperate programCacheResolutionOperate;
    
    @Autowired
    ProgramCacheCreateOrderResolutionOperate programCacheCreateOrderResolutionOperate;
    
    @Autowired
    private DelayOrderCancelSend delayOrderCancelSend;
    
    @Autowired
    private CreateOrderSend createOrderSend;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProgramShowTimeService programShowTimeService;
    
    @Autowired
    private TicketCategoryService ticketCategoryService;
    
    @Autowired
    private SeatService seatService;
    
    @Autowired
    private RedisCache redisCache;
    
    public List<TicketCategoryVo> getTicketCategoryList(ProgramOrderCreateDto programOrderCreateDto, Date showTime){
        List<TicketCategoryVo> getTicketCategoryVoList = new ArrayList<>();
        List<TicketCategoryVo> ticketCategoryVoList =
                ticketCategoryService.selectTicketCategoryListByProgramIdMultipleCache(programOrderCreateDto.getProgramId(),
                        showTime);
        Map<Long, TicketCategoryVo> ticketCategoryVoMap =
                ticketCategoryVoList.stream()
                        .collect(Collectors.toMap(TicketCategoryVo::getId, ticketCategoryVo -> ticketCategoryVo));
        List<SeatDto> seatDtoList = programOrderCreateDto.getSeatDtoList();
        if (CollectionUtil.isNotEmpty(seatDtoList)) {
            for (SeatDto seatDto : seatDtoList) {
                TicketCategoryVo ticketCategoryVo = ticketCategoryVoMap.get(seatDto.getTicketCategoryId());
                if (Objects.nonNull(ticketCategoryVo)) {
                    getTicketCategoryVoList.add(ticketCategoryVo);
                }else {
                    throw new DismaiFrameException(BaseCode.TICKET_CATEGORY_NOT_EXIST_V2);
                }
            }
        } else {
            TicketCategoryVo ticketCategoryVo = ticketCategoryVoMap.get(programOrderCreateDto.getTicketCategoryId());
            if (Objects.nonNull(ticketCategoryVo)) {
                getTicketCategoryVoList.add(ticketCategoryVo);
            }else {
                throw new DismaiFrameException(BaseCode.TICKET_CATEGORY_NOT_EXIST_V2);
            }
        }
        return getTicketCategoryVoList;
    }
    
    public String create(ProgramOrderCreateDto programOrderCreateDto) {
        ProgramShowTime programShowTime =
                programShowTimeService.selectProgramShowTimeByProgramIdMultipleCache(programOrderCreateDto.getProgramId());
        List<TicketCategoryVo> getTicketCategoryList = 
                getTicketCategoryList(programOrderCreateDto,programShowTime.getShowTime());
        BigDecimal parameterOrderPrice = new BigDecimal("0");
        BigDecimal databaseOrderPrice = new BigDecimal("0");
        List<SeatVo> purchaseSeatList = new ArrayList<>();
        List<SeatDto> seatDtoList = programOrderCreateDto.getSeatDtoList();
        List<SeatVo> seatVoList = new ArrayList<>();
        Map<String, Long> ticketCategoryRemainNumber = new HashMap<>(16);
        for (TicketCategoryVo ticketCategory : getTicketCategoryList) {
            List<SeatVo> allSeatVoList = 
                    seatService.selectSeatResolution(programOrderCreateDto.getProgramId(), ticketCategory.getId(), 
                            DateUtils.countBetweenSecond(DateUtils.now(), programShowTime.getShowTime()), TimeUnit.SECONDS);
            seatVoList.addAll(allSeatVoList.stream().
                    filter(seatVo -> seatVo.getSellStatus().equals(SellStatus.NO_SOLD.getCode())).toList());
            ticketCategoryRemainNumber.putAll(ticketCategoryService.getRedisRemainNumberResolution(
                    programOrderCreateDto.getProgramId(),ticketCategory.getId()));
        }
        if (CollectionUtil.isNotEmpty(seatDtoList)) {
            Map<Long, Long> seatTicketCategoryDtoCount = seatDtoList.stream()
                    .collect(Collectors.groupingBy(SeatDto::getTicketCategoryId, Collectors.counting()));
            for (Entry<Long, Long> entry : seatTicketCategoryDtoCount.entrySet()) {
                Long ticketCategoryId = entry.getKey();
                Long purchaseCount = entry.getValue();
                Long remainNumber = Optional.ofNullable(ticketCategoryRemainNumber.get(String.valueOf(ticketCategoryId)))
                        .orElseThrow(() -> new DismaiFrameException(BaseCode.TICKET_CATEGORY_NOT_EXIST_V2));
                if (purchaseCount > remainNumber) {
                    throw new DismaiFrameException(BaseCode.TICKET_REMAIN_NUMBER_NOT_SUFFICIENT);
                }
            }
            Map<String, SeatVo> seatVoMap = seatVoList.stream().collect(Collectors
                    .toMap(seat -> seat.getRowCode() + "-" + seat.getColCode(), seat -> seat, (v1, v2) -> v2));
            for (SeatDto seatDto : seatDtoList) {
                SeatVo seatVo = seatVoMap.get(seatDto.getRowCode() + "-" + seatDto.getColCode());
                if (Objects.isNull(seatVo)) {
                    throw new DismaiFrameException(BaseCode.SEAT_IS_NOT_NOT_SOLD);
                }
                purchaseSeatList.add(seatVo);
                parameterOrderPrice = parameterOrderPrice.add(seatDto.getPrice());
                databaseOrderPrice = databaseOrderPrice.add(seatVo.getPrice());
            }
            if (parameterOrderPrice.compareTo(databaseOrderPrice) != 0) {
                throw new DismaiFrameException(BaseCode.PRICE_ERROR);
            }
        }else {
            Long ticketCategoryId = programOrderCreateDto.getTicketCategoryId();
            Integer ticketCount = programOrderCreateDto.getTicketCount();
            Long remainNumber = Optional.ofNullable(ticketCategoryRemainNumber.get(String.valueOf(ticketCategoryId)))
                    .orElseThrow(() -> new DismaiFrameException(BaseCode.TICKET_CATEGORY_NOT_EXIST_V2));
            if (ticketCount > remainNumber) {
                throw new DismaiFrameException(BaseCode.TICKET_REMAIN_NUMBER_NOT_SUFFICIENT);
            }
            purchaseSeatList = SeatMatch.findAdjacentSeatVos(seatVoList.stream().filter(seatVo ->
                    Objects.equals(seatVo.getTicketCategoryId(), ticketCategoryId)).collect(Collectors.toList()), ticketCount);
            if (purchaseSeatList.size() < ticketCount) {
                throw new DismaiFrameException(BaseCode.SEAT_OCCUPY);
            }
        }
        updateProgramCacheDataResolution(programOrderCreateDto.getProgramId(),purchaseSeatList,OrderStatus.NO_PAY);
        return doCreate(programOrderCreateDto,purchaseSeatList);
    }
    
    
    public String createNew(ProgramOrderCreateDto programOrderCreateDto) {
        List<SeatVo> purchaseSeatList = createOrderOperateProgramCacheResolution(programOrderCreateDto, 0);
        return doCreate(programOrderCreateDto,purchaseSeatList);
    }

    public String createNewAsync(ProgramOrderCreateDto programOrderCreateDto, int shardId) {
        Long tcId = programOrderCreateDto.getTicketCategoryId();
        if (tcId != null) {
            String totalKey = RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_TICKET_TOTAL_REMAIN,
                    programOrderCreateDto.getProgramId(), tcId).getRelKey();
            Long remain = redisCache.get(RedisKeyBuild.createRedisKey(
                    RedisKeyManage.PROGRAM_TICKET_TOTAL_REMAIN,
                    programOrderCreateDto.getProgramId(), tcId), Long.class);
            if (remain != null && remain <= 0) {
                throw new DismaiFrameException(BaseCode.TICKET_REMAIN_NUMBER_NOT_SUFFICIENT);
            }
        }
        List<SeatVo> purchaseSeatList = createOrderOperateProgramCacheResolution(programOrderCreateDto, shardId);
        return doCreateV2(programOrderCreateDto,purchaseSeatList);
    }
    
    public List<SeatVo> createOrderOperateProgramCacheResolution(ProgramOrderCreateDto programOrderCreateDto, int shardId){
        ProgramShowTime programShowTime =
                programShowTimeService.selectProgramShowTimeByProgramIdMultipleCache(programOrderCreateDto.getProgramId());
        List<TicketCategoryVo> getTicketCategoryList =
                getTicketCategoryList(programOrderCreateDto,programShowTime.getShowTime());
        for (TicketCategoryVo ticketCategory : getTicketCategoryList) {
            seatService.selectSeatResolution(programOrderCreateDto.getProgramId(), ticketCategory.getId(),
                            DateUtils.countBetweenSecond(DateUtils.now(), programShowTime.getShowTime()), TimeUnit.SECONDS, shardId);
            ticketCategoryService.getRedisRemainNumberResolution(
                    programOrderCreateDto.getProgramId(),ticketCategory.getId(), shardId);
        }
        Long programId = programOrderCreateDto.getProgramId();
        List<SeatDto> seatDtoList = programOrderCreateDto.getSeatDtoList();
        List<String> keys = new ArrayList<>();
        // String[] data initialization moved to before invocation
        JSONArray jsonArray = new JSONArray();
        JSONArray addSeatDatajsonArray = new JSONArray();
        Long ticketCategoryId = null;
        Integer ticketCount = null;
        if (CollectionUtil.isNotEmpty(seatDtoList)) {
            keys.add("1");
            Map<Long, List<SeatDto>> seatTicketCategoryDtoCount = seatDtoList.stream()
                    .collect(Collectors.groupingBy(SeatDto::getTicketCategoryId));
            for (Entry<Long, List<SeatDto>> entry : seatTicketCategoryDtoCount.entrySet()) {
                ticketCategoryId = entry.getKey();
                ticketCount = entry.getValue().size();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("programTicketRemainNumberHashKey",RedisKeyBuild.createRedisKey(
                        RedisKeyManage.PROGRAM_TICKET_REMAIN_NUMBER_HASH_RESOLUTION, programId, ticketCategoryId, shardId).getRelKey());
                jsonObject.put("ticketCategoryId",String.valueOf(ticketCategoryId));
                jsonObject.put("ticketCount",ticketCount);
                jsonArray.add(jsonObject);
                
                JSONObject seatDatajsonObject = new JSONObject();
                seatDatajsonObject.put("seatNoSoldHashKey",RedisKeyBuild.createRedisKey(
                        RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH, programId, ticketCategoryId, shardId).getRelKey());
                seatDatajsonObject.put("seatDataList",JSON.toJSONString(entry.getValue()));
                addSeatDatajsonArray.add(seatDatajsonObject);
            }
        }else {
            keys.add("2");
            ticketCategoryId = programOrderCreateDto.getTicketCategoryId();
            ticketCount = programOrderCreateDto.getTicketCount();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("programTicketRemainNumberHashKey",RedisKeyBuild.createRedisKey(
                    RedisKeyManage.PROGRAM_TICKET_REMAIN_NUMBER_HASH_RESOLUTION, programId, ticketCategoryId, shardId).getRelKey());
            jsonObject.put("ticketCategoryId",String.valueOf(ticketCategoryId));
            jsonObject.put("ticketCount",ticketCount);
            jsonObject.put("seatNoSoldHashKey",RedisKeyBuild.createRedisKey(
                    RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH, programId, ticketCategoryId, shardId).getRelKey());
            jsonArray.add(jsonObject);
        }
        keys.add(RedisKeyBuild.getRedisKey(RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH));
        keys.add(RedisKeyBuild.getRedisKey(RedisKeyManage.PROGRAM_SEAT_LOCK_RESOLUTION_HASH));
        keys.add(String.valueOf(programOrderCreateDto.getProgramId()));
        keys.add(String.valueOf(shardId));
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_TICKET_TOTAL_REMAIN,
                programId, ticketCategoryId).getRelKey());
        String[] data = new String[3];
        data[0] = JSON.toJSONString(jsonArray);
        data[1] = JSON.toJSONString(addSeatDatajsonArray);
        data[2] = JSON.toJSONString(Optional.ofNullable(programOrderCreateDto.getIdNumberList()).orElse(new ArrayList<>()));
        ProgramCacheCreateOrderData programCacheCreateOrderData = 
                programCacheCreateOrderResolutionOperate.programCacheOperate(keys, data);
        if (!Objects.equals(programCacheCreateOrderData.getCode(), BaseCode.SUCCESS.getCode())) {
            throw new DismaiFrameException(Objects.requireNonNull(BaseCode.getRc(programCacheCreateOrderData.getCode())));
        }
        return programCacheCreateOrderData.getPurchaseSeatList();
    }
    
    private String doCreate(ProgramOrderCreateDto programOrderCreateDto,List<SeatVo> purchaseSeatList){
        OrderCreateDto orderCreateDto = buildCreateOrderParam(programOrderCreateDto, purchaseSeatList);
        
        String orderNumber = createOrderByRpc(orderCreateDto,purchaseSeatList);
        
        DelayOrderCancelDto delayOrderCancelDto = new DelayOrderCancelDto();
        delayOrderCancelDto.setOrderNumber(orderCreateDto.getOrderNumber());
        delayOrderCancelSend.sendMessage(JSON.toJSONString(delayOrderCancelDto));
        
        return orderNumber;
    }
    
    private String doCreateV2(ProgramOrderCreateDto programOrderCreateDto,List<SeatVo> purchaseSeatList){
        OrderCreateDto orderCreateDto = buildCreateOrderParam(programOrderCreateDto, purchaseSeatList);
        
        String orderNumber = createOrderByMq(orderCreateDto,purchaseSeatList);
        
        DelayOrderCancelDto delayOrderCancelDto = new DelayOrderCancelDto();
        delayOrderCancelDto.setOrderNumber(orderCreateDto.getOrderNumber());
        delayOrderCancelSend.sendMessage(JSON.toJSONString(delayOrderCancelDto));
        
        return orderNumber;
    }
    
    private OrderCreateDto buildCreateOrderParam(ProgramOrderCreateDto programOrderCreateDto,List<SeatVo> purchaseSeatList){
        ProgramVo programVo = programService.simpleGetProgramAndShowMultipleCache(programOrderCreateDto.getProgramId());
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderNumber(uidGenerator.getOrderNumber(programOrderCreateDto.getUserId(),ORDER_TABLE_COUNT));
        orderCreateDto.setProgramId(programOrderCreateDto.getProgramId());
        orderCreateDto.setProgramItemPicture(programVo.getItemPicture());
        orderCreateDto.setUserId(programOrderCreateDto.getUserId());
        orderCreateDto.setProgramTitle(programVo.getTitle());
        orderCreateDto.setProgramPlace(programVo.getPlace());
        orderCreateDto.setProgramShowTime(programVo.getShowTime());
        orderCreateDto.setProgramPermitChooseSeat(programVo.getPermitChooseSeat());
        BigDecimal databaseOrderPrice =
                purchaseSeatList.stream().map(SeatVo::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        orderCreateDto.setOrderPrice(databaseOrderPrice);
        orderCreateDto.setCreateOrderTime(DateUtils.now());
        
        List<Long> ticketUserIdList = programOrderCreateDto.getTicketUserIdList();
        if (ticketUserIdList.size() != purchaseSeatList.size()) {
            throw new DismaiFrameException(BaseCode.TICKET_USER_COUNT_UNEQUAL_SEAT_COUNT);
        }
        List<OrderTicketUserCreateDto> orderTicketUserCreateDtoList = new ArrayList<>();
        for (int i = 0; i < ticketUserIdList.size(); i++) {
            Long ticketUserId = ticketUserIdList.get(i);
            OrderTicketUserCreateDto orderTicketUserCreateDto = new OrderTicketUserCreateDto();
            orderTicketUserCreateDto.setOrderNumber(orderCreateDto.getOrderNumber());
            orderTicketUserCreateDto.setProgramId(programOrderCreateDto.getProgramId());
            orderTicketUserCreateDto.setUserId(programOrderCreateDto.getUserId());
            orderTicketUserCreateDto.setTicketUserId(ticketUserId);
            SeatVo seatVo =
                    Optional.ofNullable(purchaseSeatList.get(i))
                            .orElseThrow(() -> new DismaiFrameException(BaseCode.SEAT_NOT_EXIST));
            orderTicketUserCreateDto.setSeatId(seatVo.getId());
            orderTicketUserCreateDto.setSeatInfo(seatVo.getRowCode()+"排"+seatVo.getColCode()+"列");
            orderTicketUserCreateDto.setTicketCategoryId(seatVo.getTicketCategoryId());
            orderTicketUserCreateDto.setOrderPrice(seatVo.getPrice());
            orderTicketUserCreateDto.setCreateOrderTime(DateUtils.now());
            orderTicketUserCreateDtoList.add(orderTicketUserCreateDto);
        }
        
        orderCreateDto.setOrderTicketUserCreateDtoList(orderTicketUserCreateDtoList);
        
        return orderCreateDto;
    }
    
    private String createOrderByRpc(OrderCreateDto orderCreateDto,List<SeatVo> purchaseSeatList){
        ApiResponse<String> createOrderResponse = orderClient.create(orderCreateDto);
        if (!Objects.equals(createOrderResponse.getCode(), BaseCode.SUCCESS.getCode())) {
            log.error("创建订单失败 需人工处理 orderCreateDto : {}",JSON.toJSONString(orderCreateDto));
            updateProgramCacheDataResolution(orderCreateDto.getProgramId(),purchaseSeatList,OrderStatus.CANCEL);
            throw new DismaiFrameException(createOrderResponse);
        }
        return createOrderResponse.getData();
    }
    
    private String createOrderByMq(OrderCreateDto orderCreateDto,List<SeatVo> purchaseSeatList){
        CreateOrderMqDomain createOrderMqDomain = new CreateOrderMqDomain();
        CountDownLatch latch = new CountDownLatch(1);
        createOrderSend.sendMessage(JSON.toJSONString(orderCreateDto),sendResult -> {
            try {
                createOrderMqDomain.orderNumber = String.valueOf(orderCreateDto.getOrderNumber());
                assert sendResult != null;
                log.info("创建订单kafka发送消息成功 topic : {}",sendResult.getRecordMetadata().topic());
            } finally {
                latch.countDown();
            }
        },ex -> {
            log.error("创建订单kafka发送消息失败 error",ex);
            log.error("创建订单失败 需人工处理 orderCreateDto : {}",JSON.toJSONString(orderCreateDto));
            updateProgramCacheDataResolution(orderCreateDto.getProgramId(),purchaseSeatList,OrderStatus.CANCEL);
            createOrderMqDomain.DismaiFrameException = new DismaiFrameException(ex);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("createOrderByMq InterruptedException",e);
            throw new DismaiFrameException(e);
        }
        if (Objects.nonNull(createOrderMqDomain.DismaiFrameException)) {
            throw createOrderMqDomain.DismaiFrameException;
        }
        return createOrderMqDomain.orderNumber;
    }
    
    private void updateProgramCacheDataResolution(Long programId,List<SeatVo> seatVoList,OrderStatus orderStatus){
        if (!(Objects.equals(orderStatus.getCode(), OrderStatus.NO_PAY.getCode()) ||
                Objects.equals(orderStatus.getCode(), OrderStatus.CANCEL.getCode()))) {
            throw new DismaiFrameException(BaseCode.OPERATE_ORDER_STATUS_NOT_PERMIT);
        }
        List<String> keys = new ArrayList<>();
        keys.add("#");
        
        String[] data = new String[3];
        Map<Long, Long> ticketCategoryCountMap =
                seatVoList.stream().collect(Collectors.groupingBy(SeatVo::getTicketCategoryId, Collectors.counting()));
        JSONArray jsonArray = new JSONArray();
        ticketCategoryCountMap.forEach((k,v) -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("programTicketRemainNumberHashKey",RedisKeyBuild.createRedisKey(
                    RedisKeyManage.PROGRAM_TICKET_REMAIN_NUMBER_HASH_RESOLUTION, programId, k, 0).getRelKey());
            jsonObject.put("ticketCategoryId",String.valueOf(k));
            if (Objects.equals(orderStatus.getCode(), OrderStatus.NO_PAY.getCode())) {
                jsonObject.put("count","-" + v);
            } else if (Objects.equals(orderStatus.getCode(), OrderStatus.CANCEL.getCode())) {
                jsonObject.put("count",v);
            }
            jsonArray.add(jsonObject);
        });
        Map<Long, List<SeatVo>> seatVoMap = 
                seatVoList.stream().collect(Collectors.groupingBy(SeatVo::getTicketCategoryId));
        JSONArray delSeatIdjsonArray = new JSONArray();
        JSONArray addSeatDatajsonArray = new JSONArray();
        seatVoMap.forEach((k,v) -> {
            JSONObject delSeatIdjsonObject = new JSONObject();
            JSONObject seatDatajsonObject = new JSONObject();
            String seatHashKeyDel = "";
            String seatHashKeyAdd = "";
            if (Objects.equals(orderStatus.getCode(), OrderStatus.NO_PAY.getCode())) {
                seatHashKeyDel = (RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH, programId, k, 0).getRelKey());
                seatHashKeyAdd = (RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_LOCK_RESOLUTION_HASH, programId, k, 0).getRelKey());
                for (SeatVo seatVo : v) {
                    seatVo.setSellStatus(SellStatus.LOCK.getCode());
                }
            } else if (Objects.equals(orderStatus.getCode(), OrderStatus.CANCEL.getCode())) {
                seatHashKeyDel = (RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_LOCK_RESOLUTION_HASH, programId, k, 0).getRelKey());
                seatHashKeyAdd = (RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH, programId, k, 0).getRelKey());
                for (SeatVo seatVo : v) {
                    seatVo.setSellStatus(SellStatus.NO_SOLD.getCode());
                }
            }
            delSeatIdjsonObject.put("seatHashKeyDel",seatHashKeyDel);
            delSeatIdjsonObject.put("seatIdList",v.stream().map(SeatVo::getId).map(String::valueOf).collect(Collectors.toList()));
            delSeatIdjsonArray.add(delSeatIdjsonObject);
            seatDatajsonObject.put("seatHashKeyAdd",seatHashKeyAdd);
            List<String> seatDataList = new ArrayList<>();
            for (SeatVo seatVo : v) {
                seatDataList.add(String.valueOf(seatVo.getId()));
                seatDataList.add(JSON.toJSONString(seatVo));
            }
            seatDatajsonObject.put("seatDataList",seatDataList);
            addSeatDatajsonArray.add(seatDatajsonObject);
        });
        
        data[0] = JSON.toJSONString(jsonArray);
        data[1] = JSON.toJSONString(delSeatIdjsonArray);
        data[2] = JSON.toJSONString(addSeatDatajsonArray);
        programCacheResolutionOperate.programCacheOperate(keys,data);
    }
}
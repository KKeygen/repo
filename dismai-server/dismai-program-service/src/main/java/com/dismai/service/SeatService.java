package com.dismai.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dismai.core.RedisKeyManage;
import com.dismai.dto.ProgramGetDto;
import com.dismai.dto.SeatAddDto;
import com.dismai.dto.SeatBatchAddDto;
import com.dismai.dto.SeatBatchRelateInfoAddDto;
import com.dismai.dto.SeatListDto;
import com.dismai.entity.ProgramShowTime;
import com.dismai.entity.Seat;
import com.dismai.enums.BaseCode;
import com.dismai.enums.BusinessStatus;
import com.dismai.enums.SeatType;
import com.dismai.enums.SellStatus;
import com.dismai.exception.DismaiFrameException;
import com.dismai.mapper.SeatMapper;
import com.dismai.redis.RedisCache;
import com.dismai.redis.RedisKeyBuild;
import com.dismai.service.lua.ProgramSeatCacheData;
import com.dismai.servicelock.LockType;
import com.dismai.servicelock.annotion.ServiceLock;
import com.dismai.threadlocal.BaseParameterHolder;
import com.dismai.util.DateUtils;
import com.dismai.util.ServiceLockTool;
import com.dismai.util.StringUtil;
import com.dismai.vo.ProgramVo;
import com.dismai.vo.SeatRelateInfoVo;
import com.dismai.vo.SeatVo;
import com.dismai.vo.TicketCategoryVo;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dismai.constant.Constant.USER_ID;
import static com.dismai.core.DistributedLockConstants.GET_SEAT_LOCK;
import static com.dismai.core.DistributedLockConstants.SEAT_LOCK;

@Service
public class SeatService extends ServiceImpl<SeatMapper, Seat> {
    
    @Autowired
    private UidGenerator uidGenerator;
    
    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SeatMapper seatMapper;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProgramShowTimeService programShowTimeService;
    
    @Autowired
    private ServiceLockTool serviceLockTool;
    
    @Autowired
    private TicketCategoryService ticketCategoryService;
    
    @Autowired
    private ProgramSeatCacheData programSeatCacheData;
    
    @Value("${admin.user.ids:}")
    private String adminUserIds;
    
    /**
     * 添加座位
     * */
    public Long add(SeatAddDto seatAddDto) {
        LambdaQueryWrapper<Seat> seatLambdaQueryWrapper = Wrappers.lambdaQuery(Seat.class)
                .eq(Seat::getProgramId, seatAddDto.getProgramId())
                .eq(Seat::getRowCode, seatAddDto.getRowCode())
                .eq(Seat::getColCode, seatAddDto.getColCode());
        Seat seat = seatMapper.selectOne(seatLambdaQueryWrapper);
        if (Objects.nonNull(seat)) {
            throw new DismaiFrameException(BaseCode.SEAT_IS_EXIST);
        }
        seat = new Seat();
        BeanUtil.copyProperties(seatAddDto,seat);
        seat.setId(uidGenerator.getUid());
        seatMapper.insert(seat);
        return seat.getId();
    }
    
    @ServiceLock(lockType= LockType.Read,name = SEAT_LOCK,keys = {"#programId","#ticketCategoryId"})
    public List<SeatVo> selectSeatResolution(Long programId,Long ticketCategoryId,Long expireTime,TimeUnit timeUnit) {
        return selectSeatResolution(programId, ticketCategoryId, expireTime, timeUnit, 0);
    }
    
    @ServiceLock(lockType= LockType.Read,name = SEAT_LOCK,keys = {"#programId","#ticketCategoryId","#shardId"})
    public List<SeatVo> selectSeatResolution(Long programId,Long ticketCategoryId,Long expireTime,TimeUnit timeUnit, Integer shardId) {
        List<SeatVo> seatVoList = getSeatVoListByCacheResolution(programId,ticketCategoryId, shardId);
        if (CollectionUtil.isNotEmpty(seatVoList)) {
            return seatVoList;
        }
        RLock lock = serviceLockTool.getLock(LockType.Reentrant, GET_SEAT_LOCK, new String[]{String.valueOf(programId),
                String.valueOf(ticketCategoryId)});
        lock.lock();
        try {
            seatVoList = getSeatVoListByCacheResolution(programId,ticketCategoryId, shardId);
            if (CollectionUtil.isNotEmpty(seatVoList)) {
                return seatVoList;
            }
            LambdaQueryWrapper<Seat> seatLambdaQueryWrapper =
                    Wrappers.lambdaQuery(Seat.class).eq(Seat::getProgramId, programId)
                            .eq(Seat::getTicketCategoryId,ticketCategoryId)
                            .eq(Seat::getShardId, shardId);
            List<Seat> seats = seatMapper.selectList(seatLambdaQueryWrapper);
            for (Seat seat : seats) {
                SeatVo seatVo = new SeatVo();
                BeanUtil.copyProperties(seat, seatVo);
                seatVo.setSeatTypeName(SeatType.getMsg(seat.getSeatType()));
                seatVoList.add(seatVo);
            }
            Map<Integer, List<SeatVo>> seatMap = seatVoList.stream().collect(Collectors.groupingBy(SeatVo::getSellStatus));
            List<SeatVo> noSoldSeatVoList = seatMap.get(SellStatus.NO_SOLD.getCode());
            List<SeatVo> lockSeatVoList = seatMap.get(SellStatus.LOCK.getCode());
            List<SeatVo> soldSeatVoList = seatMap.get(SellStatus.SOLD.getCode());
            if (CollectionUtil.isNotEmpty(noSoldSeatVoList)) {
                redisCache.putHash(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH, 
                                programId,ticketCategoryId,shardId),noSoldSeatVoList.stream()
                                .collect(Collectors.toMap(s -> String.valueOf(s.getId()),s -> s,(v1,v2) -> v2))
                        ,expireTime, timeUnit);
            }
            if (CollectionUtil.isNotEmpty(lockSeatVoList)) {
                redisCache.putHash(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_LOCK_RESOLUTION_HASH, 
                                programId,ticketCategoryId,shardId),lockSeatVoList.stream()
                                .collect(Collectors.toMap(s -> String.valueOf(s.getId()),s -> s,(v1,v2) -> v2))
                        ,expireTime, timeUnit);
            }
            if (CollectionUtil.isNotEmpty(soldSeatVoList)) {
                redisCache.putHash(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_SOLD_RESOLUTION_HASH, 
                                programId,ticketCategoryId,shardId)
                        ,soldSeatVoList.stream()
                                .collect(Collectors.toMap(s -> String.valueOf(s.getId()),s -> s,(v1,v2) -> v2))
                        ,expireTime, timeUnit);
            }
            seatVoList = seatVoList.stream().sorted(Comparator.comparingInt(SeatVo::getRowCode)
                    .thenComparingInt(SeatVo::getColCode)).collect(Collectors.toList());
            return seatVoList;
        }finally {
            lock.unlock();
        }
    }
    
    public List<SeatVo> getSeatVoListByCacheResolution(Long programId,Long ticketCategoryId){
        return getSeatVoListByCacheResolution(programId, ticketCategoryId, 0);
    }
    
    public List<SeatVo> getSeatVoListByCacheResolution(Long programId,Long ticketCategoryId, Integer shardId){
        List<String> keys = new ArrayList<>(4);
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_NO_SOLD_RESOLUTION_HASH,
                programId, ticketCategoryId, shardId).getRelKey());
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_LOCK_RESOLUTION_HASH,
                programId, ticketCategoryId, shardId).getRelKey());
        keys.add(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_SEAT_SOLD_RESOLUTION_HASH,
                programId, ticketCategoryId, shardId).getRelKey());
        return programSeatCacheData.getData(keys, new String[]{});
    }
    
    public SeatRelateInfoVo relateInfo(SeatListDto seatListDto) {
        SeatRelateInfoVo seatRelateInfoVo = new SeatRelateInfoVo();
        ProgramVo programVo = 
                redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM,seatListDto.getProgramId()),ProgramVo.class);
        if (Objects.isNull(programVo)){
            ProgramGetDto programGetDto = new ProgramGetDto();
            programGetDto.setId(seatListDto.getProgramId());
            programVo = programService.detail(programGetDto);
        }
        ProgramShowTime programShowTime = programShowTimeService.selectProgramShowTimeByProgramId(seatListDto.getProgramId());
        List<TicketCategoryVo> ticketCategoryVoList = ticketCategoryService
                .selectTicketCategoryListByProgramIdMultipleCache(programVo.getId(),programShowTime.getShowTime());
        
        List<SeatVo> seatVos = new ArrayList<>();
        for (TicketCategoryVo ticketCategoryVo : ticketCategoryVoList) {
            seatVos.addAll(selectSeatResolution(seatListDto.getProgramId(),ticketCategoryVo.getId(),
                    DateUtils.countBetweenSecond(DateUtils.now(), programShowTime.getShowTime()), TimeUnit.SECONDS));
        }
        
        
        if (programVo.getPermitChooseSeat().equals(BusinessStatus.NO.getCode())) {
            String userId = BaseParameterHolder.getParameter(USER_ID);
            if (StringUtil.isEmpty(userId) || !Arrays.asList(adminUserIds.split(",")).contains(userId)) {
                throw new DismaiFrameException(BaseCode.PROGRAM_NOT_ALLOW_CHOOSE_SEAT);
            }
        }
        
        Map<String, List<SeatVo>> seatVoMap =
                seatVos.stream().collect(Collectors.groupingBy(seatVo -> seatVo.getPrice().toString()));
        seatRelateInfoVo.setProgramId(programVo.getId());
        seatRelateInfoVo.setPlace(programVo.getPlace());
        seatRelateInfoVo.setShowTime(programShowTime.getShowTime());
        seatRelateInfoVo.setShowWeekTime(programShowTime.getShowWeekTime());
        seatRelateInfoVo.setPriceList(seatVoMap.keySet().stream().sorted().collect(Collectors.toList()));
        seatRelateInfoVo.setSeatVoMap(seatVoMap);
        return seatRelateInfoVo;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchAdd(SeatBatchAddDto seatBatchAddDto) {
        Long programId = seatBatchAddDto.getProgramId();
        List<SeatBatchRelateInfoAddDto> seatBatchRelateInfoAddDtoList = seatBatchAddDto.getSeatBatchRelateInfoAddDtoList();
        
        
        int rowIndex = 0;
        int seatCounter = 0;
        for (SeatBatchRelateInfoAddDto seatBatchRelateInfoAddDto : seatBatchRelateInfoAddDtoList) {
            Long ticketCategoryId = seatBatchRelateInfoAddDto.getTicketCategoryId();
            BigDecimal price = seatBatchRelateInfoAddDto.getPrice();
            Integer count = seatBatchRelateInfoAddDto.getCount();
            
            int colCount = 10;
            int rowCount = (count + colCount - 1) / colCount;
            int remaining = count;
            
            for (int i = 1; i <= rowCount; i++) {
                rowIndex++;
                int colsInThisRow = Math.min(colCount, remaining);
                for (int j = 1; j <= colsInThisRow; j++) {
                    Seat seat = new Seat();
                    seat.setProgramId(programId);
                    seat.setTicketCategoryId(ticketCategoryId);
                    seat.setRowCode(rowIndex);
                    seat.setColCode(j);
                    seat.setShardId(seatCounter % 10);
                    seat.setSeatType(1);
                    seat.setPrice(price);
                    seat.setSellStatus(SellStatus.NO_SOLD.getCode());
                    seatMapper.insert(seat);
                    seatCounter++;
                }
                remaining -= colsInThisRow;
            }
        }
        for (SeatBatchRelateInfoAddDto dto : seatBatchRelateInfoAddDtoList) {
            String totalKey = RedisKeyBuild.createRedisKey(RedisKeyManage.PROGRAM_TICKET_TOTAL_REMAIN,
                    programId, dto.getTicketCategoryId()).getRelKey();
            stringRedisTemplate.opsForValue().increment(totalKey, dto.getCount());
        }
        
        return true;
    }
}

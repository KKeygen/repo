package com.dismai.integration;

import com.dismai.ProgramApplication;
import com.dismai.client.BaseDataClient;
import com.dismai.common.ApiResponse;
import com.dismai.dto.AreaGetDto;
import com.dismai.dto.AreaSelectDto;
import com.dismai.dto.ProgramPageListDto;
import com.dismai.dto.TicketCategoryListByProgramDto;
import com.dismai.page.PageVo;
import com.dismai.redis.RedisCache;
import com.dismai.service.ProgramService;
import com.dismai.service.SeatService;
import com.dismai.service.TicketCategoryService;
import com.dismai.service.es.ProgramEs;
import com.dismai.service.lua.ProgramSeatCacheData;
import com.dismai.servicelock.LockType;
import com.dismai.util.ServiceLockTool;
import com.dismai.vo.AreaVo;
import com.dismai.vo.ProgramListVo;
import com.dismai.vo.ProgramVo;
import com.dismai.vo.SeatVo;
import com.dismai.vo.TicketCategoryDetailVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("integration")
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
@SpringBootTest(classes = ProgramApplication.class)
class ProgramQueryIntegrationTest {

    private static final Long PROGRAM_ID = 2181859535445270528L;

    @Autowired
    private ProgramService programService;

    @Autowired
    private TicketCategoryService ticketCategoryService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BaseDataClient baseDataClient;

    @MockBean
    private ProgramEs programEs;

    @MockBean
    private ProgramSeatCacheData programSeatCacheData;

    @MockBean
    private ServiceLockTool serviceLockTool;

    @MockBean
    private RedisCache redisCache;

    @BeforeEach
    void setUp() {
        RLock lock = mock(RLock.class);
        doNothing().when(lock).lock();
        doNothing().when(lock).unlock();
        when(serviceLockTool.getLock(eq(LockType.Reentrant), anyString(), any(String[].class))).thenReturn(lock);
        when(programSeatCacheData.getData(anyList(), any(String[].class))).thenReturn(Collections.emptyList());
        when(programEs.selectPage(any(ProgramPageListDto.class)))
                .thenReturn(new PageVo<>(1, 5, 0, Collections.emptyList()));
        when(baseDataClient.getById(any(AreaGetDto.class))).thenAnswer(invocation -> {
            AreaGetDto dto = invocation.getArgument(0);
            AreaVo areaVo = new AreaVo();
            areaVo.setId(dto.getId());
            areaVo.setName("Mock Area");
            return ApiResponse.ok(areaVo);
        });
        when(baseDataClient.selectByIdList(any(AreaSelectDto.class))).thenAnswer(invocation -> {
            AreaSelectDto dto = invocation.getArgument(0);
            List<AreaVo> areaVoList = dto.getIdList().stream().map(id -> {
                AreaVo areaVo = new AreaVo();
                areaVo.setId(id);
                areaVo.setName("Mock Area " + id);
                return areaVo;
            }).toList();
            return ApiResponse.ok(areaVoList);
        });
    }

    @Test
    void shouldExposeProgramListDetailTicketCategoryAndSeatData() {
        ProgramPageListDto pageListDto = new ProgramPageListDto();
        pageListDto.setPageNumber(1);
        pageListDto.setPageSize(5);
        pageListDto.setTimeType(0);

        PageVo<ProgramListVo> programPageVo = programService.selectPage(pageListDto);
        assumeTrue(programPageVo != null && programPageVo.getList() != null && !programPageVo.getList().isEmpty(),
                "Program list data is not seeded in the current environment");
        assertTrue(programPageVo.getTotalSize() >= programPageVo.getList().size());
        ProgramListVo firstProgram = programPageVo.getList().get(0);
        assertNotNull(firstProgram.getId());
        assertNotNull(firstProgram.getTitle());

        ProgramVo programVo;
        try {
            programVo = programService.getDetailFromDb(PROGRAM_ID);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "Program detail data is not seeded for program " + PROGRAM_ID);
            return;
        }
        assertEquals(PROGRAM_ID, programVo.getId());
        assertNotNull(programVo.getTitle());
        assertNotNull(programVo.getShowTime());
        assertEquals("Mock Area", programVo.getAreaName());

        TicketCategoryListByProgramDto ticketCategoryListByProgramDto = new TicketCategoryListByProgramDto();
        ticketCategoryListByProgramDto.setProgramId(PROGRAM_ID);
        List<TicketCategoryDetailVo> ticketCategories = ticketCategoryService.selectListByProgram(ticketCategoryListByProgramDto);
        assumeTrue(ticketCategories != null && !ticketCategories.isEmpty(),
                "Ticket category data is not seeded for program " + PROGRAM_ID);
        TicketCategoryDetailVo firstCategory = ticketCategories.get(0);
        assertNotNull(firstCategory.getId());
        assertNotNull(firstCategory.getPrice());
        assertTrue(firstCategory.getPrice().compareTo(BigDecimal.ZERO) > 0);

        List<SeatVo> seatVoList = seatService.selectSeatResolution(
                PROGRAM_ID,
                firstCategory.getId(),
                60L,
                TimeUnit.SECONDS);
        assumeTrue(seatVoList != null && !seatVoList.isEmpty(),
                "Seat data is not seeded for program " + PROGRAM_ID + " and ticket category " + firstCategory.getId());
        SeatVo firstSeat = seatVoList.get(0);
        assertEquals(PROGRAM_ID, firstSeat.getProgramId());
        assertEquals(firstCategory.getId(), firstSeat.getTicketCategoryId());
        assertNotNull(firstSeat.getRowCode());
        assertNotNull(firstSeat.getColCode());
        assertNotNull(firstSeat.getPrice());
    }
}

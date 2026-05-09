package com.dismai.service.lua;

import com.dismai.vo.SeatVo;
import lombok.Data;

import java.util.List;

@Data
public class ProgramCacheCreateOrderData {

    private Integer code;
    
    private List<SeatVo> purchaseSeatList;
}

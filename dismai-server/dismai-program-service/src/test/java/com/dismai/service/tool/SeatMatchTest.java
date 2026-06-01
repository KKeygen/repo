package com.dismai.service.tool;

import com.dismai.vo.SeatVo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SeatMatchTest {

    @Test
    void shouldPreferAdjacentSeatsInSameRow() {
        List<SeatVo> result = SeatMatch.findAdjacentSeatVos(List.of(
                seat(2, 4),
                seat(1, 5),
                seat(1, 2),
                seat(1, 3),
                seat(1, 4)
        ), 3);

        assertEquals(List.of(2, 3, 4), result.stream().map(SeatVo::getColCode).toList());
        assertEquals(List.of(1, 1, 1), result.stream().map(SeatVo::getRowCode).toList());
    }

    @Test
    void shouldUseSameColumnWhenNoRowHasEnoughAdjacentSeats() {
        List<SeatVo> result = SeatMatch.findAdjacentSeatVos(List.of(
                seat(1, 1),
                seat(1, 3),
                seat(2, 3),
                seat(3, 3)
        ), 3);

        assertEquals(List.of(1, 2, 3), result.stream().map(SeatVo::getRowCode).toList());
        assertEquals(List.of(3, 3, 3), result.stream().map(SeatVo::getColCode).toList());
    }

    @Test
    void shouldRejectInvalidSeatCount() {
        assertThrows(IllegalArgumentException.class, () -> SeatMatch.findAdjacentSeatVos(List.of(seat(1, 1)), 0));
    }

    @Test
    void shouldRejectInsufficientSeats() {
        assertThrows(RuntimeException.class, () -> SeatMatch.findAdjacentSeatVos(List.of(seat(1, 1)), 2));
    }

    private static SeatVo seat(int rowCode, int colCode) {
        SeatVo seat = new SeatVo();
        seat.setRowCode(rowCode);
        seat.setColCode(colCode);
        return seat;
    }
}

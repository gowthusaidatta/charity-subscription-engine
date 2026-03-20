package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.enums.DrawMode;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DrawResponse(
        UUID id,
        String monthKey,
        LocalDate drawDate,
        DrawMode mode,
        List<Integer> winningNumbers,
        boolean published,
        long winners3,
        long winners4,
        long winners5
) {
}

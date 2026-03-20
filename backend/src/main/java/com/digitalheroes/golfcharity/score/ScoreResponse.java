package com.digitalheroes.golfcharity.score;

import java.time.LocalDate;
import java.util.UUID;

public record ScoreResponse(
        UUID id,
        Integer scoreValue,
        LocalDate scoreDate
) {
}

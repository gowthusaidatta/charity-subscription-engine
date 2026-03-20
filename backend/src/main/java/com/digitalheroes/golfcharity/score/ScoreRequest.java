package com.digitalheroes.golfcharity.score;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScoreRequest(
        @NotNull(message = "Score value is required")
        @Min(value = 1, message = "Score must be between 1 and 45")
        @Max(value = 45, message = "Score must be between 1 and 45")
        Integer scoreValue,

        @NotNull(message = "Score date is required")
        LocalDate scoreDate
) {
}

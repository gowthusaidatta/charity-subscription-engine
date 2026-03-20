package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.enums.DrawMode;
import jakarta.validation.constraints.NotNull;

public record DrawExecuteRequest(
        String monthKey,

        @NotNull(message = "Draw mode is required")
        DrawMode mode,

        boolean publish
) {
}

package com.digitalheroes.golfcharity.common;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String path,
        LocalDateTime timestamp
) {
}

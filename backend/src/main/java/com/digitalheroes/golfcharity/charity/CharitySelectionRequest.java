package com.digitalheroes.golfcharity.charity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CharitySelectionRequest(
        @NotNull(message = "Charity id is required")
        UUID charityId,

        @NotNull(message = "Contribution percent is required")
        @DecimalMin(value = "10.00", message = "Minimum contribution is 10%")
        BigDecimal contributionPercent
) {
}

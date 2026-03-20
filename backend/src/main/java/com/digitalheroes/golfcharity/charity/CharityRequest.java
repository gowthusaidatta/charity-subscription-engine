package com.digitalheroes.golfcharity.charity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CharityRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Slug is required")
        String slug,

        @NotBlank(message = "Description is required")
        String description,

        String imageUrl,

        @NotNull(message = "Active status is required")
        Boolean active,

        @NotNull(message = "Featured status is required")
        Boolean featured,

        @NotNull(message = "Contribution percent is required")
        @DecimalMin(value = "10.00", message = "Minimum contribution is 10%")
        BigDecimal contributionPercent
) {
}

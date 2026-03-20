package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutSessionRequest(
        @NotNull(message = "Plan is required")
        SubscriptionPlan plan,

        @NotBlank(message = "Success URL is required")
        String successUrl,

        @NotBlank(message = "Cancel URL is required")
        String cancelUrl
) {
}

package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;

public record SubscriptionActivateRequest(
        @NotNull(message = "Plan is required")
        SubscriptionPlan plan
) {
}

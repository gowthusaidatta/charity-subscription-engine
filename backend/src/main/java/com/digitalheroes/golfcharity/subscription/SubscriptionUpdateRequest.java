package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubscriptionUpdateRequest(
        @NotNull(message = "Plan is required")
        SubscriptionPlan plan,

        @NotNull(message = "Status is required")
        SubscriptionStatus status,

        @NotNull(message = "Renewal date is required")
        LocalDate renewalDate
) {
}

package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;

import java.time.LocalDate;

public record SubscriptionStatusResponse(
        SubscriptionPlan plan,
        SubscriptionStatus status,
        LocalDate renewalDate
) {
}

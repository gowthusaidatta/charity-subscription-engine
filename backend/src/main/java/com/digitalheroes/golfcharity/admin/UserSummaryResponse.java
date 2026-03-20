package com.digitalheroes.golfcharity.admin;

import com.digitalheroes.golfcharity.enums.Role;

import java.math.BigDecimal;
import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        String selectedCharity,
        BigDecimal contributionPercent
) {
}

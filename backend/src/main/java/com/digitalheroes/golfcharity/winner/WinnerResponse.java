package com.digitalheroes.golfcharity.winner;

import com.digitalheroes.golfcharity.enums.PayoutStatus;
import com.digitalheroes.golfcharity.enums.VerificationStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record WinnerResponse(
        UUID id,
        UUID userId,
        String userEmail,
        Integer matchCount,
        BigDecimal prizeAmount,
        VerificationStatus verificationStatus,
        PayoutStatus payoutStatus,
        String proofUrl
) {
}

package com.digitalheroes.golfcharity.admin;

import com.digitalheroes.golfcharity.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;

public record WinnerAdminUpdateRequest(
        @NotNull(message = "Verification status is required")
        VerificationStatus verificationStatus,
        String proofUrl
) {
}

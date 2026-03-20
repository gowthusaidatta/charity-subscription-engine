package com.digitalheroes.golfcharity.winner;

import jakarta.validation.constraints.NotBlank;

public record WinnerProofSubmitRequest(
        @NotBlank(message = "Proof URL is required")
        String proofUrl
) {
}

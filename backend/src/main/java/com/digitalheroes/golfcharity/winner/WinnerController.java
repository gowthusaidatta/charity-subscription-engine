package com.digitalheroes.golfcharity.winner;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/winners")
public class WinnerController {

    private final WinnerService winnerService;

    public WinnerController(WinnerService winnerService) {
        this.winnerService = winnerService;
    }

    @PostMapping("/{winnerId}/proof")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitProof(
            @PathVariable UUID winnerId,
            @Valid @RequestBody WinnerProofSubmitRequest request,
            Authentication authentication
    ) {
        winnerService.submitProof(authentication.getName(), winnerId, request);
    }
}

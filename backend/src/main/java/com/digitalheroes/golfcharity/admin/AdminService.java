package com.digitalheroes.golfcharity.admin;

import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import com.digitalheroes.golfcharity.enums.PayoutStatus;
import com.digitalheroes.golfcharity.winner.Winner;
import com.digitalheroes.golfcharity.winner.WinnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminService {

    private final WinnerRepository winnerRepository;

    public AdminService(WinnerRepository winnerRepository) {
        this.winnerRepository = winnerRepository;
    }

    @Transactional
    public void verifyWinner(UUID winnerId, WinnerAdminUpdateRequest request) {
        Winner winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Winner not found"));

        winner.setVerificationStatus(request.verificationStatus());
        if (request.proofUrl() != null && !request.proofUrl().isBlank()) {
            winner.setProofUrl(request.proofUrl());
        }
        winnerRepository.save(winner);
    }

    @Transactional
    public void markPaid(UUID winnerId) {
        Winner winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Winner not found"));

        winner.setPayoutStatus(PayoutStatus.PAID);
        winnerRepository.save(winner);
    }
}

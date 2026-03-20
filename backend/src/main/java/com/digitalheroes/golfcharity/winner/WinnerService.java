package com.digitalheroes.golfcharity.winner;

import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WinnerService {

    private final WinnerRepository winnerRepository;

    public WinnerService(WinnerRepository winnerRepository) {
        this.winnerRepository = winnerRepository;
    }

    @Transactional
    public void submitProof(String email, java.util.UUID winnerId, WinnerProofSubmitRequest request) {
        Winner winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Winner not found"));

        if (!winner.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new ResourceNotFoundException("Winner not found");
        }

        winner.setProofUrl(request.proofUrl().trim());
        winnerRepository.save(winner);
    }
}

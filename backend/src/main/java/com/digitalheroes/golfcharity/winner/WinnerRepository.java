package com.digitalheroes.golfcharity.winner;

import com.digitalheroes.golfcharity.enums.PayoutStatus;
import com.digitalheroes.golfcharity.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WinnerRepository extends JpaRepository<Winner, UUID> {
    List<Winner> findByDrawId(UUID drawId);
    List<Winner> findByUserId(UUID userId);
    void deleteByDrawId(UUID drawId);
    long countByVerificationStatus(VerificationStatus status);
    long countByPayoutStatus(PayoutStatus status);
}

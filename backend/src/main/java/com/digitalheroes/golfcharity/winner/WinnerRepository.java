package com.digitalheroes.golfcharity.winner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WinnerRepository extends JpaRepository<Winner, UUID> {
    List<Winner> findByDrawId(UUID drawId);
    List<Winner> findByUserId(UUID userId);
}

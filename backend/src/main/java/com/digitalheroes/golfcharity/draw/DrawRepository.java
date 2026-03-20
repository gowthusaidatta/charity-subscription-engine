package com.digitalheroes.golfcharity.draw;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DrawRepository extends JpaRepository<Draw, UUID> {
    Optional<Draw> findByMonthKey(String monthKey);
    Optional<Draw> findTopByOrderByDrawDateDesc();
    Optional<Draw> findTopByPublishedTrueOrderByDrawDateDesc();
}

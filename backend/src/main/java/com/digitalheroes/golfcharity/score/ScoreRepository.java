package com.digitalheroes.golfcharity.score;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {
    List<Score> findTop5ByUserIdOrderByScoreDateDescCreatedAtDesc(UUID userId);
    List<Score> findByUserIdOrderByScoreDateDescCreatedAtDesc(UUID userId);
    Optional<Score> findFirstByUserIdOrderByScoreDateAscCreatedAtAsc(UUID userId);
    long countByUserId(UUID userId);
}

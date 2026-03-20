package com.digitalheroes.golfcharity.score;

import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final UserService userService;

    public ScoreService(ScoreRepository scoreRepository, UserService userService) {
        this.scoreRepository = scoreRepository;
        this.userService = userService;
    }

    @Transactional
    public ScoreResponse addScore(String email, ScoreRequest request) {
        User user = userService.getByEmail(email);

        Score score = new Score();
        score.setUser(user);
        score.setScoreValue(request.scoreValue());
        score.setScoreDate(request.scoreDate());
        Score saved = scoreRepository.save(score);

        enforceFiveScoreLimit(user.getId());
        return toResponse(saved);
    }

    public List<ScoreResponse> getLastFiveScores(String email) {
        User user = userService.getByEmail(email);
        return scoreRepository.findTop5ByUserIdOrderByScoreDateDescCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ScoreResponse updateScore(String email, java.util.UUID scoreId, ScoreRequest request) {
        User user = userService.getByEmail(email);
        Score score = scoreRepository.findById(scoreId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new com.digitalheroes.golfcharity.common.ResourceNotFoundException("Score not found"));

        score.setScoreValue(request.scoreValue());
        score.setScoreDate(request.scoreDate());
        Score saved = scoreRepository.save(score);

        enforceFiveScoreLimit(user.getId());
        return toResponse(saved);
    }

    private void enforceFiveScoreLimit(java.util.UUID userId) {
        long count = scoreRepository.countByUserId(userId);
        while (count > 5) {
            Score oldest = scoreRepository.findFirstByUserIdOrderByScoreDateAscCreatedAtAsc(userId)
                    .orElse(null);
            if (oldest == null) {
                break;
            }
            scoreRepository.delete(oldest);
            count = scoreRepository.countByUserId(userId);
        }
    }

    private ScoreResponse toResponse(Score score) {
        return new ScoreResponse(score.getId(), score.getScoreValue(), score.getScoreDate());
    }
}

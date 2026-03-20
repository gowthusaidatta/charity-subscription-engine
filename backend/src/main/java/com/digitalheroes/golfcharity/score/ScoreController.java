package com.digitalheroes.golfcharity.score;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    public ScoreResponse addScore(@Valid @RequestBody ScoreRequest request, Authentication authentication) {
        return scoreService.addScore(authentication.getName(), request);
    }

    @GetMapping
    public List<ScoreResponse> getMyScores(Authentication authentication) {
        return scoreService.getLastFiveScores(authentication.getName());
    }

    @PutMapping("/{scoreId}")
    public ScoreResponse updateScore(@PathVariable UUID scoreId, @Valid @RequestBody ScoreRequest request, Authentication authentication) {
        return scoreService.updateScore(authentication.getName(), scoreId, request);
    }
}

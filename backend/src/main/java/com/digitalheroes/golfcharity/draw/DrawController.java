package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.winner.WinnerResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/draws")
public class DrawController {

    private final DrawService drawService;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    @GetMapping("/latest")
    public DrawResponse getLatest() {
        return drawService.getLatestDraw();
    }

    @GetMapping("/my-results")
    public List<WinnerResponse> getMyResults(Authentication authentication) {
        return drawService.getMyResults(authentication.getName());
    }

    @PostMapping("/admin/execute")
    @PreAuthorize("hasRole('ADMIN')")
    public DrawResponse executeDraw(@Valid @RequestBody DrawExecuteRequest request) {
        return drawService.executeDraw(request);
    }

    @GetMapping("/admin/{drawId}/winners")
    @PreAuthorize("hasRole('ADMIN')")
    public List<WinnerResponse> getWinners(@PathVariable UUID drawId) {
        return drawService.getWinnersForDraw(drawId);
    }
}

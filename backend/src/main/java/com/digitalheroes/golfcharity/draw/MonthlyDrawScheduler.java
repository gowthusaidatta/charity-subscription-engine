package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.enums.DrawMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonthlyDrawScheduler {

    private final DrawService drawService;

    @Value("${app.draw.monthly-mode:RANDOM}")
    private String monthlyMode;

    public MonthlyDrawScheduler(DrawService drawService) {
        this.drawService = drawService;
    }

    @Scheduled(cron = "${app.draw.monthly-cron:0 0 1 1 * *}")
    public void runMonthlyDraw() {
        DrawExecuteRequest request = new DrawExecuteRequest(
                null,
                DrawMode.valueOf(monthlyMode.toUpperCase()),
                true
        );

        try {
            drawService.executeDraw(request);
        } catch (Exception ignored) {
            // Keep scheduler resilient; draw can be executed manually by admin if needed.
        }
    }
}

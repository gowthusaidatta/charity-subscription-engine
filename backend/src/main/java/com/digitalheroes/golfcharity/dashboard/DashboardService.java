package com.digitalheroes.golfcharity.dashboard;

import com.digitalheroes.golfcharity.draw.DrawService;
import com.digitalheroes.golfcharity.score.ScoreService;
import com.digitalheroes.golfcharity.subscription.SubscriptionService;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final SubscriptionService subscriptionService;
    private final ScoreService scoreService;
    private final DrawService drawService;
    private final UserService userService;

    public DashboardService(
            SubscriptionService subscriptionService,
            ScoreService scoreService,
            DrawService drawService,
            UserService userService
    ) {
        this.subscriptionService = subscriptionService;
        this.scoreService = scoreService;
        this.drawService = drawService;
        this.userService = userService;
    }

    public DashboardResponse getDashboard(String email) {
        User user = userService.getByEmail(email);
        return new DashboardResponse(
                subscriptionService.getMySubscription(email),
                scoreService.getLastFiveScores(email),
                user.getSelectedCharity() != null ? user.getSelectedCharity().getName() : null,
                user.getCharityContributionPercent(),
                drawService.getMyResults(email)
        );
    }
}

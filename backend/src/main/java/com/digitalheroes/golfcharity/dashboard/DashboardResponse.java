package com.digitalheroes.golfcharity.dashboard;

import com.digitalheroes.golfcharity.score.ScoreResponse;
import com.digitalheroes.golfcharity.subscription.SubscriptionStatusResponse;
import com.digitalheroes.golfcharity.winner.WinnerResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        SubscriptionStatusResponse subscription,
        List<ScoreResponse> scores,
        String selectedCharity,
        BigDecimal charityContributionPercent,
        List<WinnerResponse> recentWinnings
) {
}

package com.digitalheroes.golfcharity.admin;

import java.math.BigDecimal;

public record AdminAnalyticsResponse(
        long totalUsers,
        long totalDraws,
        long totalPublishedDraws,
        BigDecimal totalPrizePool,
        BigDecimal totalCharityContributions,
        long pendingWinnerVerifications,
        long pendingPayouts,
        BigDecimal currentRollover
) {
}

package com.digitalheroes.golfcharity.admin;

import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import com.digitalheroes.golfcharity.enums.PayoutStatus;
import com.digitalheroes.golfcharity.enums.VerificationStatus;
import com.digitalheroes.golfcharity.draw.Draw;
import com.digitalheroes.golfcharity.draw.DrawRepository;
import com.digitalheroes.golfcharity.notification.EmailNotificationService;
import com.digitalheroes.golfcharity.user.UserRepository;
import com.digitalheroes.golfcharity.winner.Winner;
import com.digitalheroes.golfcharity.winner.WinnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final WinnerRepository winnerRepository;
    private final EmailNotificationService emailNotificationService;
    private final UserRepository userRepository;
    private final DrawRepository drawRepository;

    public AdminService(
            WinnerRepository winnerRepository,
            EmailNotificationService emailNotificationService,
            UserRepository userRepository,
            DrawRepository drawRepository
    ) {
        this.winnerRepository = winnerRepository;
        this.emailNotificationService = emailNotificationService;
        this.userRepository = userRepository;
        this.drawRepository = drawRepository;
    }

    @Transactional
    public void verifyWinner(UUID winnerId, WinnerAdminUpdateRequest request) {
        Winner winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Winner not found"));

        winner.setVerificationStatus(request.verificationStatus());
        if (request.proofUrl() != null && !request.proofUrl().isBlank()) {
            winner.setProofUrl(request.proofUrl());
        }
        winnerRepository.save(winner);
        emailNotificationService.notifyWinnerVerification(winner);
    }

    @Transactional
    public void markPaid(UUID winnerId) {
        Winner winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Winner not found"));

        winner.setPayoutStatus(PayoutStatus.PAID);
        winnerRepository.save(winner);
        emailNotificationService.notifyWinnerPayout(winner);
    }

        public AdminAnalyticsResponse analytics() {
        List<Draw> draws = drawRepository.findAll();

        BigDecimal totalPrizePool = draws.stream()
            .map(Draw::getTotalPoolAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCharityContributions = draws.stream()
            .map(Draw::getTotalCharityContributionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPublishedDraws = draws.stream().filter(Draw::isPublished).count();

        BigDecimal currentRollover = drawRepository.findTopByPublishedTrueOrderByDrawDateDesc()
            .map(Draw::getRolloverOutAmount)
            .orElse(BigDecimal.ZERO);

        return new AdminAnalyticsResponse(
            userRepository.count(),
            draws.size(),
            totalPublishedDraws,
            totalPrizePool,
            totalCharityContributions,
            winnerRepository.countByVerificationStatus(VerificationStatus.PENDING),
            winnerRepository.countByPayoutStatus(PayoutStatus.PENDING),
            currentRollover
        );
        }
}

package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.common.BadRequestException;
import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import com.digitalheroes.golfcharity.enums.DrawMode;
import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import com.digitalheroes.golfcharity.notification.EmailNotificationService;
import com.digitalheroes.golfcharity.score.Score;
import com.digitalheroes.golfcharity.score.ScoreRepository;
import com.digitalheroes.golfcharity.subscription.Subscription;
import com.digitalheroes.golfcharity.subscription.SubscriptionRepository;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.winner.Winner;
import com.digitalheroes.golfcharity.winner.WinnerRepository;
import com.digitalheroes.golfcharity.winner.WinnerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DrawService {

    private final DrawRepository drawRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ScoreRepository scoreRepository;
    private final WinnerRepository winnerRepository;
    private final EmailNotificationService emailNotificationService;

    @Value("${app.billing.monthly-fee}")
    private BigDecimal monthlyFee;

    @Value("${app.billing.yearly-fee}")
    private BigDecimal yearlyFee;

    @Value("${app.prize.pool-percent}")
    private BigDecimal prizePoolPercent;

    @Value("${app.prize.tier5-percent}")
    private BigDecimal tier5Percent;

    @Value("${app.prize.tier4-percent}")
    private BigDecimal tier4Percent;

    @Value("${app.prize.tier3-percent}")
    private BigDecimal tier3Percent;

    public DrawService(
            DrawRepository drawRepository,
            SubscriptionRepository subscriptionRepository,
            ScoreRepository scoreRepository,
            WinnerRepository winnerRepository,
            EmailNotificationService emailNotificationService
    ) {
        this.drawRepository = drawRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.scoreRepository = scoreRepository;
        this.winnerRepository = winnerRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional
    public DrawResponse executeDraw(DrawExecuteRequest request) {
        String monthKey = request.monthKey() == null || request.monthKey().isBlank()
                ? YearMonth.now().toString()
                : request.monthKey();

        Optional<Draw> existingDrawOpt = drawRepository.findByMonthKey(monthKey);
        if (existingDrawOpt.isPresent() && existingDrawOpt.get().isPublished()) {
            throw new BadRequestException("Published draw already exists for month: " + monthKey);
        }

        List<Integer> winningNumbers = generateWinningNumbers(request.mode());
        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);

        BigDecimal rolloverIn = drawRepository.findTopByPublishedTrueOrderByDrawDateDesc()
            .map(Draw::getRolloverOutAmount)
            .orElse(BigDecimal.ZERO);

        BigDecimal basePool = activeSubscriptions.stream()
            .map(this::monthlyEquivalentFee)
            .map(this::poolContribution)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCharityContribution = activeSubscriptions.stream()
            .map(this::charityContribution)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPool = basePool.add(rolloverIn).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tier5Pool = percentage(totalPool, tier5Percent);
        BigDecimal tier4Pool = percentage(totalPool, tier4Percent);
        BigDecimal tier3Pool = percentage(totalPool, tier3Percent);

        Draw draw = existingDrawOpt.orElseGet(Draw::new);
        draw.setMonthKey(monthKey);
        draw.setDrawDate(LocalDate.now());
        draw.setMode(request.mode());
        draw.setWinningNumbers(toCsv(winningNumbers));
        draw.setPublished(request.publish());
        draw.setActiveSubscriberCount(activeSubscriptions.size());
        draw.setTotalPoolAmount(totalPool);
        draw.setTier5PoolAmount(tier5Pool);
        draw.setTier4PoolAmount(tier4Pool);
        draw.setTier3PoolAmount(tier3Pool);
        draw.setRolloverInAmount(rolloverIn);
        draw.setTotalCharityContributionAmount(totalCharityContribution);

        Draw savedDraw = drawRepository.save(draw);
        winnerRepository.deleteByDrawId(savedDraw.getId());

        List<User> tier3Users = new ArrayList<>();
        List<User> tier4Users = new ArrayList<>();
        List<User> tier5Users = new ArrayList<>();
        Set<Integer> winningSet = new HashSet<>(winningNumbers);

        for (Subscription subscription : activeSubscriptions) {
            UUID userId = subscription.getUser().getId();
            List<Score> scores = scoreRepository.findTop5ByUserIdOrderByScoreDateDescCreatedAtDesc(userId);
            if (scores.size() < 5) {
                continue;
            }

            Set<Integer> userSet = scores.stream().map(Score::getScoreValue).collect(Collectors.toSet());
            long matches = userSet.stream().filter(winningSet::contains).count();

            if (matches >= 3) {
                if (matches == 5) {
                    tier5Users.add(subscription.getUser());
                } else if (matches == 4) {
                    tier4Users.add(subscription.getUser());
                } else {
                    tier3Users.add(subscription.getUser());
                }
            }
        }

        BigDecimal tier3PrizePerWinner = splitPool(tier3Pool, tier3Users.size());
        BigDecimal tier4PrizePerWinner = splitPool(tier4Pool, tier4Users.size());
        BigDecimal tier5PrizePerWinner = splitPool(tier5Pool, tier5Users.size());

        List<Winner> winners = new ArrayList<>();
        winners.addAll(createWinners(savedDraw, tier3Users, 3, tier3PrizePerWinner));
        winners.addAll(createWinners(savedDraw, tier4Users, 4, tier4PrizePerWinner));
        winners.addAll(createWinners(savedDraw, tier5Users, 5, tier5PrizePerWinner));

        winnerRepository.saveAll(winners);

        if (request.publish()) {
            savedDraw.setRolloverOutAmount(tier5Users.isEmpty() ? tier5Pool : BigDecimal.ZERO);
        } else {
            savedDraw.setRolloverOutAmount(BigDecimal.ZERO);
        }
        savedDraw = drawRepository.save(savedDraw);

        if (request.publish() && !winners.isEmpty()) {
            emailNotificationService.notifyDrawPublished(savedDraw.getMonthKey(), winners);
        }

        return toDrawResponse(savedDraw, tier3Users.size(), tier4Users.size(), tier5Users.size());
    }

    public DrawResponse getLatestDraw() {
        Draw draw = drawRepository.findTopByOrderByDrawDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No draw found"));
        List<Winner> winners = winnerRepository.findByDrawId(draw.getId());
        Map<Integer, Long> counts = winners.stream()
            .collect(Collectors.groupingBy(Winner::getMatchCount, Collectors.counting()));
        return toDrawResponse(draw,
            counts.getOrDefault(3, 0L).intValue(),
            counts.getOrDefault(4, 0L).intValue(),
            counts.getOrDefault(5, 0L).intValue());
    }

    public List<WinnerResponse> getWinnersForDraw(UUID drawId) {
        if (!drawRepository.existsById(drawId)) {
            throw new ResourceNotFoundException("Draw not found");
        }
        return winnerRepository.findByDrawId(drawId).stream()
                .map(this::toWinnerResponse)
                .toList();
    }

    public List<WinnerResponse> getMyResults(String email) {
        return winnerRepository.findAll().stream()
                .filter(winner -> winner.getUser().getEmail().equalsIgnoreCase(email))
                .map(this::toWinnerResponse)
                .toList();
    }

        private DrawResponse toDrawResponse(Draw draw, int winners3, int winners4, int winners5) {
        return new DrawResponse(
                draw.getId(),
                draw.getMonthKey(),
                draw.getDrawDate(),
                draw.getMode(),
                fromCsv(draw.getWinningNumbers()),
                draw.isPublished(),
            draw.getActiveSubscriberCount(),
            draw.getTotalPoolAmount(),
            draw.getTier5PoolAmount(),
            draw.getTier4PoolAmount(),
            draw.getTier3PoolAmount(),
            draw.getRolloverInAmount(),
            draw.getRolloverOutAmount(),
            draw.getTotalCharityContributionAmount(),
            winners3,
            winners4,
            winners5
        );
    }

    private WinnerResponse toWinnerResponse(Winner winner) {
        return new WinnerResponse(
                winner.getId(),
                winner.getUser().getId(),
                winner.getUser().getEmail(),
                winner.getMatchCount(),
                winner.getPrizeAmount(),
                winner.getVerificationStatus(),
                winner.getPayoutStatus(),
                winner.getProofUrl()
        );
    }

    private List<Integer> generateWinningNumbers(DrawMode mode) {
        return mode == DrawMode.WEIGHTED ? weightedNumbers() : randomNumbers();
    }

    private List<Integer> randomNumbers() {
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i <= 45; i++) {
            values.add(i);
        }
        Collections.shuffle(values);
        return values.subList(0, 5).stream().sorted().toList();
    }

    private List<Integer> weightedNumbers() {
        List<Score> allScores = scoreRepository.findAll();
        if (allScores.isEmpty()) {
            return randomNumbers();
        }

        Map<Integer, Long> frequencies = allScores.stream()
                .map(Score::getScoreValue)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Integer> result = new ArrayList<>();
        Random random = new Random();

        while (result.size() < 5) {
            long totalWeight = frequencies.values().stream().mapToLong(Long::longValue).sum();
            if (totalWeight == 0) {
                break;
            }

            long target = 1 + random.nextLong(totalWeight);
            long cumulative = 0;
            Integer selected = null;
            for (Map.Entry<Integer, Long> entry : frequencies.entrySet()) {
                cumulative += entry.getValue();
                if (target <= cumulative) {
                    selected = entry.getKey();
                    break;
                }
            }

            if (selected != null && !result.contains(selected)) {
                result.add(selected);
            }

            if (result.size() < 5 && result.size() == frequencies.size()) {
                break;
            }
        }

        if (result.size() < 5) {
            List<Integer> fallback = randomNumbers();
            for (Integer num : fallback) {
                if (!result.contains(num)) {
                    result.add(num);
                }
                if (result.size() == 5) {
                    break;
                }
            }
        }

        return result.stream().sorted().toList();
    }

    private BigDecimal monthlyEquivalentFee(Subscription subscription) {
        if (subscription.getPlan() == SubscriptionPlan.YEARLY) {
            return yearlyFee.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        }
        return monthlyFee;
    }

    private BigDecimal poolContribution(BigDecimal monthlyEquivalentFee) {
        return percentage(monthlyEquivalentFee, prizePoolPercent);
    }

    private BigDecimal charityContribution(Subscription subscription) {
        BigDecimal fee = monthlyEquivalentFee(subscription);
        BigDecimal percentage = subscription.getUser().getCharityContributionPercent();
        return percentage(fee, percentage);
    }

    private BigDecimal percentage(BigDecimal amount, BigDecimal percentage) {
        return amount.multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal splitPool(BigDecimal pool, int winners) {
        if (winners <= 0) {
            return BigDecimal.ZERO;
        }
        return pool.divide(BigDecimal.valueOf(winners), 2, RoundingMode.HALF_UP);
    }

    private List<Winner> createWinners(Draw draw, List<User> users, int matchCount, BigDecimal prizeAmount) {
        return users.stream().map(user -> {
            Winner winner = new Winner();
            winner.setDraw(draw);
            winner.setUser(user);
            winner.setMatchCount(matchCount);
            winner.setPrizeAmount(prizeAmount);
            return winner;
        }).toList();
    }

    private String toCsv(List<Integer> numbers) {
        return numbers.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private List<Integer> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }
}

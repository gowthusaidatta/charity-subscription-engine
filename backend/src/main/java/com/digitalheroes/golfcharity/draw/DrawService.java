package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.common.BadRequestException;
import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import com.digitalheroes.golfcharity.enums.DrawMode;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import com.digitalheroes.golfcharity.score.Score;
import com.digitalheroes.golfcharity.score.ScoreRepository;
import com.digitalheroes.golfcharity.subscription.Subscription;
import com.digitalheroes.golfcharity.subscription.SubscriptionRepository;
import com.digitalheroes.golfcharity.winner.Winner;
import com.digitalheroes.golfcharity.winner.WinnerRepository;
import com.digitalheroes.golfcharity.winner.WinnerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public DrawService(
            DrawRepository drawRepository,
            SubscriptionRepository subscriptionRepository,
            ScoreRepository scoreRepository,
            WinnerRepository winnerRepository
    ) {
        this.drawRepository = drawRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.scoreRepository = scoreRepository;
        this.winnerRepository = winnerRepository;
    }

    @Transactional
    public DrawResponse executeDraw(DrawExecuteRequest request) {
        String monthKey = request.monthKey() == null || request.monthKey().isBlank()
                ? YearMonth.now().toString()
                : request.monthKey();

        drawRepository.findByMonthKey(monthKey).ifPresent(existing -> {
            throw new BadRequestException("Draw already exists for month: " + monthKey);
        });

        List<Integer> winningNumbers = generateWinningNumbers(request.mode());

        Draw draw = new Draw();
        draw.setMonthKey(monthKey);
        draw.setDrawDate(LocalDate.now());
        draw.setMode(request.mode());
        draw.setWinningNumbers(toCsv(winningNumbers));
        draw.setPublished(request.publish());
        Draw savedDraw = drawRepository.save(draw);

        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);

        List<Winner> winners = new ArrayList<>();
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
                Winner winner = new Winner();
                winner.setDraw(savedDraw);
                winner.setUser(subscription.getUser());
                winner.setMatchCount((int) matches);
                winner.setPrizeAmount(prizeFor((int) matches));
                winners.add(winner);
            }
        }

        if (!winners.isEmpty()) {
            winnerRepository.saveAll(winners);
        }

        return toDrawResponse(savedDraw);
    }

    public DrawResponse getLatestDraw() {
        Draw draw = drawRepository.findTopByOrderByDrawDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No draw found"));
        return toDrawResponse(draw);
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

    private DrawResponse toDrawResponse(Draw draw) {
        List<Winner> winners = winnerRepository.findByDrawId(draw.getId());
        Map<Integer, Long> counts = winners.stream()
                .collect(Collectors.groupingBy(Winner::getMatchCount, Collectors.counting()));

        return new DrawResponse(
                draw.getId(),
                draw.getMonthKey(),
                draw.getDrawDate(),
                draw.getMode(),
                fromCsv(draw.getWinningNumbers()),
                draw.isPublished(),
                counts.getOrDefault(3, 0L),
                counts.getOrDefault(4, 0L),
                counts.getOrDefault(5, 0L)
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

    private BigDecimal prizeFor(int matchCount) {
        return switch (matchCount) {
            case 5 -> BigDecimal.valueOf(1000);
            case 4 -> BigDecimal.valueOf(300);
            default -> BigDecimal.valueOf(100);
        };
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

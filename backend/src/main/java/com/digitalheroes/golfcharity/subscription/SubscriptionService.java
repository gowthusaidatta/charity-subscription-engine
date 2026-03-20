package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserService userService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
    }

    public SubscriptionStatusResponse getMySubscription(String email) {
        User user = userService.getByEmail(email);
        Subscription subscription = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        return toResponse(subscription);
    }

    @Transactional
    public SubscriptionStatusResponse activateMySubscription(String email, SubscriptionActivateRequest request) {
        User user = userService.getByEmail(email);
        Subscription subscription = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscription.setPlan(request.plan());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setRenewalDate(request.plan().name().equals("YEARLY")
                ? LocalDate.now().plusYears(1)
                : LocalDate.now().plusMonths(1));

        return toResponse(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionStatusResponse adminUpdateSubscription(UUID userId, SubscriptionUpdateRequest request) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscription.setPlan(request.plan());
        subscription.setStatus(request.status());
        subscription.setRenewalDate(request.renewalDate());

        Subscription saved = subscriptionRepository.save(subscription);
        return toResponse(saved);
    }

    private SubscriptionStatusResponse toResponse(Subscription subscription) {
        return new SubscriptionStatusResponse(
                subscription.getPlan(),
                subscription.getStatus(),
                subscription.getRenewalDate()
        );
    }
}

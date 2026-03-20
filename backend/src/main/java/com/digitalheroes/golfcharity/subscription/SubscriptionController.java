package com.digitalheroes.golfcharity.subscription;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final StripeSubscriptionService stripeSubscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService, StripeSubscriptionService stripeSubscriptionService) {
        this.subscriptionService = subscriptionService;
        this.stripeSubscriptionService = stripeSubscriptionService;
    }

    @GetMapping("/me")
    public SubscriptionStatusResponse getMySubscription(Authentication authentication) {
        return subscriptionService.getMySubscription(authentication.getName());
    }

    @PostMapping("/activate")
    public SubscriptionStatusResponse activateMySubscription(
            @Valid @RequestBody SubscriptionActivateRequest request,
            Authentication authentication
    ) {
        return subscriptionService.activateMySubscription(authentication.getName(), request);
    }

    @PostMapping("/checkout-session")
    public CheckoutSessionResponse createCheckoutSession(
            @Valid @RequestBody CheckoutSessionRequest request,
            Authentication authentication
    ) {
        return stripeSubscriptionService.createCheckoutSession(request, authentication.getName());
    }

    @PutMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionStatusResponse adminUpdateSubscription(
            @PathVariable UUID userId,
            @Valid @RequestBody SubscriptionUpdateRequest request
    ) {
        return subscriptionService.adminUpdateSubscription(userId, request);
    }
}

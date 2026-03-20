package com.digitalheroes.golfcharity.subscription;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions/stripe")
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        stripeWebhookService.handleWebhook(payload, signature);
    }
}

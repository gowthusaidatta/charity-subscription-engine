package com.digitalheroes.golfcharity.subscription;

public record CheckoutSessionResponse(
        String checkoutUrl,
        String sessionId
) {
}

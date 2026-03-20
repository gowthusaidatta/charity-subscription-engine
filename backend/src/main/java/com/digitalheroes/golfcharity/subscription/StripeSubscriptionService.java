package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.common.BadRequestException;
import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeSubscriptionService {

    @Value("${app.stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${app.stripe.monthly-price-id:}")
    private String monthlyPriceId;

    @Value("${app.stripe.yearly-price-id:}")
    private String yearlyPriceId;

    public CheckoutSessionResponse createCheckoutSession(CheckoutSessionRequest request, String customerEmail) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new BadRequestException("Stripe secret key is not configured");
        }

        String priceId = request.plan() == SubscriptionPlan.YEARLY ? yearlyPriceId : monthlyPriceId;
        if (priceId == null || priceId.isBlank()) {
            throw new BadRequestException("Stripe price id is not configured for plan " + request.plan());
        }

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(request.successUrl())
                .setCancelUrl(request.cancelUrl())
                .setCustomerEmail(customerEmail)
                .putMetadata("plan", request.plan().name())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build()
                )
                .build();

        try {
            Session session = Session.create(params);
            return new CheckoutSessionResponse(session.getUrl(), session.getId());
        } catch (StripeException ex) {
            throw new BadRequestException("Failed to create Stripe checkout session: " + ex.getMessage());
        }
    }
}

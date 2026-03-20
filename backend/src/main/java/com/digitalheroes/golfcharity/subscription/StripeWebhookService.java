package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.common.BadRequestException;
import com.digitalheroes.golfcharity.enums.SubscriptionPlan;
import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class StripeWebhookService {

    @Value("${app.stripe.webhook-secret:}")
    private String webhookSecret;

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public StripeWebhookService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException ex) {
            throw new BadRequestException("Invalid Stripe webhook signature");
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(event);
            case "customer.subscription.created", "customer.subscription.updated", "customer.subscription.deleted" ->
                    handleStripeSubscriptionEvent(event);
            default -> {
                // Ignore unsupported event types
            }
        }
    }

    private void handleCheckoutCompleted(Event event) {
        StripeObject stripeObject = extractStripeObject(event);
        if (stripeObject == null) {
            return;
        }

        Session session = (Session) stripeObject;
        String email = session.getCustomerDetails() != null ? session.getCustomerDetails().getEmail() : session.getCustomerEmail();
        if (email == null || email.isBlank()) {
            return;
        }

        User user = userRepository.findByEmail(email.toLowerCase()).orElse(null);
        if (user == null) {
            return;
        }

        Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);
        if (subscription == null) {
            return;
        }

        subscription.setProviderCustomerId(session.getCustomer());
        subscription.setProviderSubscriptionId(session.getSubscription());
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        String plan = session.getMetadata() != null ? session.getMetadata().get("plan") : null;
        if ("YEARLY".equalsIgnoreCase(plan)) {
            subscription.setPlan(SubscriptionPlan.YEARLY);
            subscription.setRenewalDate(LocalDate.now().plusYears(1));
        } else {
            subscription.setPlan(SubscriptionPlan.MONTHLY);
            subscription.setRenewalDate(LocalDate.now().plusMonths(1));
        }

        subscriptionRepository.save(subscription);
    }

    private void handleStripeSubscriptionEvent(Event event) {
        StripeObject stripeObject = extractStripeObject(event);
        if (stripeObject == null) {
            return;
        }

        com.stripe.model.Subscription stripeSubscription = (com.stripe.model.Subscription) stripeObject;
        String providerSubscriptionId = stripeSubscription.getId();
        String providerCustomerId = stripeSubscription.getCustomer();

        Subscription subscription = subscriptionRepository.findByProviderSubscriptionId(providerSubscriptionId)
                .orElseGet(() -> subscriptionRepository.findByProviderCustomerId(providerCustomerId).orElse(null));

        if (subscription == null) {
            return;
        }

        subscription.setProviderCustomerId(providerCustomerId);
        subscription.setProviderSubscriptionId(providerSubscriptionId);
        subscription.setStatus(mapStatus(stripeSubscription.getStatus()));

        subscriptionRepository.save(subscription);
    }

    private SubscriptionStatus mapStatus(String stripeStatus) {
        if (stripeStatus == null) {
            return SubscriptionStatus.INACTIVE;
        }

        return switch (stripeStatus) {
            case "active", "trialing" -> SubscriptionStatus.ACTIVE;
            case "past_due", "unpaid", "incomplete_expired" -> SubscriptionStatus.LAPSED;
            case "canceled" -> SubscriptionStatus.CANCELED;
            default -> SubscriptionStatus.INACTIVE;
        };
    }

    private StripeObject extractStripeObject(Event event) {
        Optional<StripeObject> objectOpt = event.getDataObjectDeserializer().getObject();
        if (objectOpt.isPresent()) {
            return objectOpt.get();
        }

        try {
            return event.getDataObjectDeserializer().deserializeUnsafe();
        } catch (EventDataObjectDeserializationException ex) {
            return null;
        }
    }
}

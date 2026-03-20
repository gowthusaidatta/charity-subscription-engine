package com.digitalheroes.golfcharity.subscription;

import com.digitalheroes.golfcharity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUserId(UUID userId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    Optional<Subscription> findByProviderCustomerId(String providerCustomerId);
    Optional<Subscription> findByProviderSubscriptionId(String providerSubscriptionId);
}

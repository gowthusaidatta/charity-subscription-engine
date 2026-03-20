ALTER TABLE subscriptions
    ADD COLUMN IF NOT EXISTS provider_customer_id VARCHAR(150);

ALTER TABLE subscriptions
    ADD COLUMN IF NOT EXISTS provider_subscription_id VARCHAR(150);

CREATE INDEX IF NOT EXISTS idx_subscriptions_provider_customer_id
    ON subscriptions(provider_customer_id);

CREATE INDEX IF NOT EXISTS idx_subscriptions_provider_subscription_id
    ON subscriptions(provider_subscription_id);

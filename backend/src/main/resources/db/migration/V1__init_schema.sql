CREATE TABLE IF NOT EXISTS charities (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    selected_charity_id UUID,
    charity_contribution_percent NUMERIC(5, 2) NOT NULL DEFAULT 10.00,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_users_charity FOREIGN KEY (selected_charity_id) REFERENCES charities(id)
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    plan VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    renewal_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS scores (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    score_value INTEGER NOT NULL,
    score_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_scores_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_scores_user_date_created ON scores(user_id, score_date DESC, created_at DESC);

CREATE TABLE IF NOT EXISTS draws (
    id UUID PRIMARY KEY,
    month_key VARCHAR(7) NOT NULL UNIQUE,
    draw_date DATE NOT NULL,
    mode VARCHAR(20) NOT NULL,
    winning_numbers VARCHAR(100) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS winners (
    id UUID PRIMARY KEY,
    draw_id UUID NOT NULL,
    user_id UUID NOT NULL,
    match_count INTEGER NOT NULL,
    prize_amount NUMERIC(12, 2) NOT NULL,
    verification_status VARCHAR(20) NOT NULL,
    payout_status VARCHAR(20) NOT NULL,
    proof_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_winners_draw FOREIGN KEY (draw_id) REFERENCES draws(id),
    CONSTRAINT fk_winners_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_winners_draw ON winners(draw_id);
CREATE INDEX IF NOT EXISTS idx_winners_user ON winners(user_id);

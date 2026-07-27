CREATE TABLE user_account (
    id UUID PRIMARY KEY,
    email VARCHAR(180) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE job_application (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    company VARCHAR(140) NOT NULL,
    role_title VARCHAR(160) NOT NULL,
    location VARCHAR(160),
    job_url VARCHAR(500),
    source VARCHAR(80),
    status VARCHAR(30) NOT NULL,
    work_model VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    salary_min INTEGER,
    salary_max INTEGER,
    salary_currency VARCHAR(3) NOT NULL,
    applied_at DATE,
    next_action_at TIMESTAMP WITH TIME ZONE,
    notes VARCHAR(3000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_salary_range CHECK (
        salary_min IS NULL OR salary_max IS NULL OR salary_min <= salary_max
    )
);

CREATE INDEX idx_application_owner_status
    ON job_application(owner_id, status);
CREATE INDEX idx_application_owner_next_action
    ON job_application(owner_id, next_action_at);

CREATE TABLE application_event (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL REFERENCES job_application(id) ON DELETE CASCADE,
    event_type VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_event_application_occurred
    ON application_event(application_id, occurred_at DESC);

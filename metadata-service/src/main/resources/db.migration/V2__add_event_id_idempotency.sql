-- =====================================================
-- EVENT IDEMPOTENCY TABLE
-- =====================================================

CREATE TABLE public.processed_events (
    event_id varchar(100) NOT NULL,
    event_type varchar(50) NOT NULL,
    source_service varchar(50) NOT NULL,
    processed_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT processed_events_pkey PRIMARY KEY (event_id)
);
CREATE TABLE IF NOT EXISTS public.buckets (
    bucket_name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(50) NOT NULL,
    versioning_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT buckets_pkey PRIMARY KEY (bucket_name),
    CONSTRAINT ukt0eixmlhmca6880pe763sondi UNIQUE (bucket_name, owner_id)
);
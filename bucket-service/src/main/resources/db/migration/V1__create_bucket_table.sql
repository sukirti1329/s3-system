CREATE TABLE buckets (
    bucket_name VARCHAR(255) PRIMARY KEY,
    owner_id VARCHAR(50) NOT NULL,
    versioning_enabled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

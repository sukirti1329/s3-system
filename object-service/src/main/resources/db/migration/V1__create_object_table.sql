CREATE TABLE objects (
    id VARCHAR(50) PRIMARY KEY,
    bucket_name VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    checksum VARCHAR(255),
    storage_path TEXT NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_bucket_object UNIQUE (bucket_name, file_name)
);
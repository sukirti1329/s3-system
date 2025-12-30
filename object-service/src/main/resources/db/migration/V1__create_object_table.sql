CREATE TABLE object_metadata (
    id UUID PRIMARY KEY,
    object_id VARCHAR(50) UNIQUE NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(50) NOT NULL,
    access_level VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE object_tags (
    id UUID PRIMARY KEY,
    metadata_id UUID NOT NULL,
    tag VARCHAR(100) NOT NULL,
    CONSTRAINT fk_object_tags_metadata
        FOREIGN KEY (metadata_id)
        REFERENCES object_metadata(id)
        ON DELETE CASCADE
);

-- Prevent duplicate tags for same object
ALTER TABLE object_tags
ADD CONSTRAINT uq_metadata_tag UNIQUE (metadata_id, tag);

CREATE TABLE object_versions (
    id UUID PRIMARY KEY,
    metadata_id UUID NOT NULL,
    version_number INT NOT NULL,
    checksum VARCHAR(255),
    size BIGINT NOT NULL,
    storage_path TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_versions_metadata
        FOREIGN KEY (metadata_id)
        REFERENCES object_metadata(id)
        ON DELETE CASCADE
);

-- Only one active version per object
CREATE UNIQUE INDEX uq_active_version
    ON object_versions(metadata_id)
    WHERE is_active = true;

-- Prevent duplicate version numbers
ALTER TABLE object_versions
ADD CONSTRAINT uq_metadata_version UNIQUE (metadata_id, version_number);

-- ============================================================
-- INDEXES (SEARCH & PERFORMANCE)
-- ============================================================

CREATE INDEX idx_metadata_object_id
    ON object_metadata(object_id);

CREATE INDEX idx_metadata_owner
    ON object_metadata(owner_id);

CREATE INDEX idx_metadata_bucket
    ON object_metadata(bucket_name);

CREATE INDEX idx_metadata_access
    ON object_metadata(access_level);

CREATE INDEX idx_tags_tag
    ON object_tags(tag);

CREATE INDEX idx_versions_metadata
    ON object_versions(metadata_id);

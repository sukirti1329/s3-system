-- =========================================================
-- TABLE: object_metadata
-- =========================================================
CREATE TABLE object_metadata (
    id UUID NOT NULL,
    object_id VARCHAR(255) NOT NULL,
    bucket_name VARCHAR(255),
    owner_id VARCHAR(255),
    access_level VARCHAR(255),
    description VARCHAR(255),
    versioning_enabled BOOLEAN DEFAULT TRUE,
    active_version INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT object_metadata_pkey PRIMARY KEY (id),
    CONSTRAINT uk_object_metadata_object_id UNIQUE (object_id),
    CONSTRAINT object_metadata_access_level_check
        CHECK (access_level IN ('PRIVATE', 'PUBLIC', 'SHARED'))
);

-- =========================================================
-- TABLE: object_tags
-- =========================================================
CREATE TABLE object_tags (
    id UUID NOT NULL,
    tag VARCHAR(255),
    metadata_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT object_tags_pkey PRIMARY KEY (id),
    CONSTRAINT fk_object_tags_metadata
        FOREIGN KEY (metadata_id)
        REFERENCES object_metadata (id)
        ON DELETE CASCADE
);

-- =========================================================
-- TABLE: object_versions
-- =========================================================
CREATE TABLE object_versions (
    id UUID NOT NULL,
    object_id VARCHAR(255),
    version_number INT NOT NULL,
    checksum VARCHAR(255),
    storage_path VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT object_versions_pkey PRIMARY KEY (id),
    CONSTRAINT uq_object_version UNIQUE (object_id, version_number)
);

-- =========================================================
-- INDEXES (Recommended)
-- =========================================================
CREATE INDEX idx_object_metadata_object_id
    ON object_metadata (object_id);

CREATE INDEX idx_object_tags_metadata_id
    ON object_tags (metadata_id);

CREATE INDEX idx_object_versions_object_id
    ON object_versions (object_id);

-- =====================================================
-- METADATA TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.object_metadata (
    id UUID NOT NULL,
    access_level VARCHAR(255),
    bucket_name VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    object_id VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    versioning_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    active_version INTEGER,
    file_name VARCHAR(255) NOT NULL,

    CONSTRAINT object_metadata_pkey PRIMARY KEY (id),
    CONSTRAINT uk_fnjse679kkmueblihcn310arl UNIQUE (object_id),
    CONSTRAINT object_metadata_access_level_check
        CHECK (access_level IN ('PRIVATE', 'PUBLIC', 'SHARED'))
);

CREATE INDEX IF NOT EXISTS idx_object_metadata_file_name
    ON public.object_metadata(file_name);

-- =====================================================
-- TAGS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.object_tags (
    id UUID NOT NULL,
    tag VARCHAR(255),
    metadata_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT object_tags_pkey PRIMARY KEY (id),
    CONSTRAINT fkjxr0kocmj824oe2ha1f7lba1u
        FOREIGN KEY (metadata_id)
        REFERENCES public.object_metadata(id)
);

CREATE INDEX IF NOT EXISTS idx_object_tags_metadata_id
    ON public.object_tags(metadata_id);

-- =====================================================
-- VERSIONS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS public.object_versions (
    id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL,
    object_id VARCHAR(255) NOT NULL,
    version_number INTEGER NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    versioning_enabled BOOLEAN NOT NULL,

    CONSTRAINT object_versions_pkey PRIMARY KEY (id),
    CONSTRAINT uk_object_versions_object_version UNIQUE (object_id, version_number),
    CONSTRAINT ukj6g3sm60w3m51ovgy1nqncvpi UNIQUE (object_id, version_number)
);

CREATE INDEX IF NOT EXISTS idx_object_versions_owner_bucket
    ON public.object_versions(owner_id, bucket_name);

CREATE UNIQUE INDEX IF NOT EXISTS ux_object_versions_active
    ON public.object_versions(object_id)
    WHERE is_active = TRUE;

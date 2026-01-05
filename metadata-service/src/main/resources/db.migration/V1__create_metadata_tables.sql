-- =====================================================
-- METADATA TABLE
-- =====================================================

CREATE TABLE public.object_metadata (
    id uuid NOT NULL,
    access_level varchar(255) NULL,
    bucket_name varchar(255) NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description varchar(255) NULL,
    object_id varchar(255) NOT NULL,
    owner_id varchar(255) NOT NULL,
    versioning_enabled bool NOT NULL DEFAULT true,
    active_version int4 NULL,

    CONSTRAINT object_metadata_pkey
        PRIMARY KEY (id),

    CONSTRAINT uk_object_metadata_object_id
        UNIQUE (object_id),

    CONSTRAINT object_metadata_access_level_check
        CHECK (access_level IN ('PRIVATE', 'PUBLIC', 'SHARED'))
);

CREATE INDEX idx_object_metadata_object_id
    ON public.object_metadata (object_id);

-- =====================================================
-- TAGS TABLE
-- =====================================================

CREATE TABLE public.object_tags (
    id uuid NOT NULL,
    tag varchar(255) NULL,
    metadata_id uuid NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT object_tags_pkey
        PRIMARY KEY (id),

    CONSTRAINT fk_object_tags_metadata
        FOREIGN KEY (metadata_id)
        REFERENCES public.object_metadata(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_object_tags_metadata_id
    ON public.object_tags (metadata_id);

-- =====================================================
-- VERSIONS TABLE
-- =====================================================

CREATE TABLE public.object_versions (
    id uuid NOT NULL,
    object_id varchar(255) NOT NULL,
    owner_id varchar(255) NOT NULL,
    version_number int4 NOT NULL,
    is_active bool NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT object_versions_pkey
        PRIMARY KEY (id),

    CONSTRAINT uk_object_versions_object_version
        UNIQUE (object_id, version_number)
);

-- Only one active version per object
CREATE UNIQUE INDEX ux_object_versions_active
    ON public.object_versions (object_id)
    WHERE is_active = true;

CREATE TABLE IF NOT EXISTS public.objects (
    id VARCHAR(255) NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    checksum VARCHAR(255),
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    storage_path VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    content_type VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    versioning_enabled BOOLEAN NOT NULL,

    CONSTRAINT objects_pkey PRIMARY KEY (id),
    CONSTRAINT uk5w0es6pt1pcq8vnd466ip97n8 UNIQUE (bucket_name, file_name)
);

CREATE INDEX IF NOT EXISTS idx_objects_owner_id
    ON public.objects(owner_id);

CREATE INDEX IF NOT EXISTS idx_objects_versioning_enabled
    ON public.objects(versioning_enabled);
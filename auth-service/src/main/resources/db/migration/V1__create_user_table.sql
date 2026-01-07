CREATE TABLE IF NOT EXISTS public.users (
    id BIGSERIAL NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150),
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_user_id_key UNIQUE (user_id),
    CONSTRAINT users_username_key UNIQUE (username)
);
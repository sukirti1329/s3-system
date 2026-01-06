package com.s3.common.events.enums;

public enum S3EventType {
    OBJECT_CREATED,
    OBJECT_UPDATED,
    OBJECT_DELETED,

    BUCKET_UPDATED,
    BUCKET_DELETED,

    METADATA_CREATED,
    METADATA_UPDATED,
    METADATA_DELETED
}

package com.s3.event.model;

public enum S3EventType {
    OBJECT_CREATED,
    OBJECT_DELETED,
    METADATA_CREATED,
    METADATA_UPDATED,
    VERSION_CREATED,
    VERSION_ROLLBACK
}

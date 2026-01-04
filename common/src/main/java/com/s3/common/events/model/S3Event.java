package com.s3.common.events.model;

import com.s3.common.events.enums.S3EventType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class S3Event {

    /** Unique event identifier */
    private String eventId = UUID.randomUUID().toString();
    /** Type of event */
    private S3EventType eventType;
    /** Which service produced this event */
    private String sourceService;
    /** Owner (from auth context) */
    private String ownerId;
    /** When event occurred */
    private Instant occurredAt;
    /**
     * Domain-specific payload
     * Example:
     * {
     *   "checksum": "...",
     *   "storagePath": "...",
     *   "contentType": "...",
     *   "size": 1234
     * }
     */
    private Map<String, Object> payload;
}
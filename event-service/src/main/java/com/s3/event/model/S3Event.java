package com.s3.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Generic event contract shared across services.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class S3Event {

    /** Unique event identifier */
    private String eventId;
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
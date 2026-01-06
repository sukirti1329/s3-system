package com.s3.common.events.payload.object;

import com.s3.common.events.payload.S3EventPayload;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class ObjectUpdatedPayload implements S3EventPayload {

    private String objectId;
    private String bucketName;
    private String filename;

    // Optional updates
    private String description;
    private List<String> tags;
    private String accessLevel;
    private boolean versionEnabled;
}
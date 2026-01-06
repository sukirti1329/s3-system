package com.s3.common.events.payload.object;

import com.s3.common.events.payload.S3EventPayload;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
public class ObjectCreatedPayload implements S3EventPayload {

    private String objectId;
    private String bucketName;
    private String filename;
    private String description;
    private List<String> tags;
    private String accessLevel;
    private Boolean versionEnabled;
}

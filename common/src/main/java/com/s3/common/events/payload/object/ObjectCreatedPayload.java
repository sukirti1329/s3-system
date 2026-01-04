package com.s3.common.events.payload.object;

import com.s3.common.events.payload.S3EventPayload;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectCreatedPayload implements S3EventPayload {

    private String objectId;
    private String bucketName;
    private String objectKey;

    private Long size;
    private String checksum;
    private String contentType;

    private String description;
    private List<String> tags;
    private String accessLevel;
    private Boolean versionEnabled;
}

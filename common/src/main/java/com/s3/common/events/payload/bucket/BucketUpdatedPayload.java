package com.s3.common.events.payload.bucket;
import com.s3.common.events.payload.S3EventPayload;
import lombok.*;
import lombok.extern.jackson.Jacksonized;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class BucketUpdatedPayload implements S3EventPayload {
    private String bucketName;
    private boolean versioningEnabled;
}

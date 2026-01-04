package com.s3.common.events.payload.object;

import com.s3.common.events.payload.S3EventPayload;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDeletedPayload implements S3EventPayload {

    private String objectId;
    private String bucketName;
    private String objectKey;
}

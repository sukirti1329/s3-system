package com.s3.common.dto;


import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BucketDTO {

    private String bucketId;
    private String bucketName;
    private String ownerId;
    private boolean versioningEnabled;
    private Instant createdAt;
}

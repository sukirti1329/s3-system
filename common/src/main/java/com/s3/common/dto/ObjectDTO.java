package com.s3.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ObjectDTO {


    private String objectKey;
    private String bucketName;
    private String fileName;
    private long size;
    private String checksum;
    private Instant uploadedAt;
}

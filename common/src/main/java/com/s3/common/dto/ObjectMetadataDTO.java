package com.s3.common.dto;

import com.s3.common.enums.AccessLevel;
import lombok.Data;

import java.util.List;

@Data
public class ObjectMetadataDTO {

    private String objectId;
    private String bucketName;

    private String description;
    private List<String> tags;

    private AccessLevel accessLevel;
    private int activeVersion;
}

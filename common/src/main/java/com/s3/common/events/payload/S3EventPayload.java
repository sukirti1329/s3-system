package com.s3.common.events.payload;

//Marker Interface for Payloads. This gives type safety without coupling.

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.s3.common.events.payload.object.ObjectCreatedPayload;
import com.s3.common.events.payload.object.ObjectDeletedPayload;
import com.s3.common.events.payload.object.ObjectUpdatedPayload;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "payloadType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ObjectCreatedPayload.class, name = "OBJECT_CREATED"),
        @JsonSubTypes.Type(value = ObjectUpdatedPayload.class, name = "OBJECT_UPDATED"),
        @JsonSubTypes.Type(value = ObjectDeletedPayload.class, name = "OBJECT_DELETED")
})public interface S3EventPayload {
}

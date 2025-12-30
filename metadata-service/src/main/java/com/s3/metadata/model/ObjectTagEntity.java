package com.s3.metadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "object_tags")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectTagEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_id", nullable = false)
    private ObjectMetadataEntity metadata;

    private String tag;
}

package com.ptit.core_platform_service.entity;

import com.ptit.core_platform_service.entity.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JD {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jdId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String rawContent;

    @Column(columnDefinition = "TEXT")
    private String structuredContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
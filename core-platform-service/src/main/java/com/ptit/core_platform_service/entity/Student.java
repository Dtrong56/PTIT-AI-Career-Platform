package com.ptit.core_platform_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID studentId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "student")
    @Builder.Default
    private List<CV> cvs = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    @Builder.Default
    private List<JD> jds = new ArrayList<>();

    // methods
    public UUID getId() {
        return studentId;
    }
}
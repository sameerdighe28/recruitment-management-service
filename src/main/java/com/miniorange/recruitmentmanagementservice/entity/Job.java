package com.miniorange.recruitmentmanagementservice.entity;

import com.miniorange.recruitmentmanagementservice.enums.JobCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "job_skillsets", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skillset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory category;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}


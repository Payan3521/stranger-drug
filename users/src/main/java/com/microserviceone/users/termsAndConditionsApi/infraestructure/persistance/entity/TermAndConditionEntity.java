package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms_and_conditions")
@NoArgsConstructor
@Getter
@Setter
public class TermAndConditionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String version;

    @Column(name = "create_term", nullable = false)
    private LocalDateTime createTerm;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String type;
} 

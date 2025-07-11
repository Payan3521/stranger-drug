package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "accepted_terms")
@NoArgsConstructor
@Getter
@Setter
public class AcceptedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "fecha_hora_aceptacion", nullable = false)
    private LocalDateTime fechaHoraDeAceptacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private TermAndConditionEntity terminoAceptado;

    @Column(nullable = false)
    private String ip;
} 
package com.healthcare.healthcare.model;

import com.healthcare.healthcare.eum.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "Request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(nullable = false, name = "patient_id")
    private Long patientID;

    @Column(nullable = false, name = "doctor_id")
    private Long doctorID;

    @Column(nullable = false, name = "proficiency_id")
    private Long proficiencyID;

    @Column(nullable = false, name = "requirements")
    private String requirements;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "request_status")
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false, updatable = false, name = "created_at")
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}


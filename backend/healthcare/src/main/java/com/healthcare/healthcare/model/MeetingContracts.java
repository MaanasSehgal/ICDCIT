package com.healthcare.healthcare.model;

import com.healthcare.healthcare.eum.Status;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "MeetingContract")
public class MeetingContracts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @JoinColumn(name = "request_id", referencedColumnName = "requestId", nullable = false)
    private Long requestId;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

    @Column(name = "meeting_details", nullable = false)
    private String meetingDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "completed_by_patient", nullable = false)
    private Boolean completedByPatient = false;

    @Column(name = "completed_by_doctor", nullable = false)
    private Byte completedByDoctor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}

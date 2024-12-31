package com.healthcare.healthcare.model;

import com.healthcare.healthcare.eum.Resolution;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Dispute")
public class Disputes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispute_id", nullable = false, unique = true)
    private Long disputeId;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(name = "filed_by", nullable = false)
    private Long userId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "evidence")
    private String evidence;

    @Column(name = "resolved_by_admin")
    private Byte resolvedByAdmin;

    @Column(name = "resolution")
    @Enumerated(EnumType.STRING)
    private Resolution resolution;

    @Column(name = "resolution_comment")
    private String resolutionComment;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = new Date();
    }
}

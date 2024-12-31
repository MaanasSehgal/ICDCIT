package com.healthcare.healthcare.model;

import com.healthcare.healthcare.eum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // Renamed table to avoid reserved keyword conflict
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "username")
    private String userName;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_role")
    private Role role;

    @Lob // Indicates large object storage
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(length = 500, name = "bio")
    private String bio;

    @Temporal(TemporalType.TIMESTAMP) // Specify temporal precision
    @Column(nullable = false, updatable = false, name = "created_at")
    private Date createdAt;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date(); // Set creation timestamp
    }
}

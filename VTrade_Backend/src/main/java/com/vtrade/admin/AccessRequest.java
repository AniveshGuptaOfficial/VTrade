package com.vtrade.admin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    /** "delivery_staff" | "store" */
    @Column(nullable = false)
    private String requestedRole;

    @Column(length = 1000)
    private String message;

    /** pending | approved | rejected — admin updates this manually in Supabase after reviewing */
    private String status = "pending";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
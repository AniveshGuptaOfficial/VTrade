package com.vtrade.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String subject;

    @Column(length = 2000, nullable = false)
    private String message;

    /** new | read | replied — admin updates this manually in Supabase after handling */
    private String status = "new";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
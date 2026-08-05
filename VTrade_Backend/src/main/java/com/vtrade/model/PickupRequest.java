package com.vtrade.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId;

    /** Amazon Kart | Flipkart Hub | VIT Book Store | Other */
    @Column(nullable = false)
    private String kartName;

    @Column(length = 1000, nullable = false)
    private String productDetails;

    private String trackingId;

    private String deliveryBoyName;

    private String deliveryBoyPhone;

    @Column(nullable = false)
    private Double amount;

    /** paid | cod */
    @Column(nullable = false)
    private String paymentMode;

    @Column(nullable = false)
    private String requesterPhone;

    @Column(nullable = false)
    private String hostelBlock;

    @Column(nullable = false)
    private String roomNumber;

    @Column(length = 1000)
    private String notes;

    private Double feeEstimate;

    /** pending | assigned | picked_up | delivered | cancelled */
    private String status = "pending";

    /** worker assigned to this request */
    private Long assignedWorkerId;
    private String assignedWorkerName;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

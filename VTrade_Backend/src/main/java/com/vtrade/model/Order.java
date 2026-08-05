package com.vtrade.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double tax;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String hostelBlock;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String phone;

    private String landmark;

    /** upi | card | cod */
    private String paymentMethod = "upi";

    /** placed | assigned | out_for_delivery | delivered | cancelled */
    private String status = "placed";

    /** OTP shown at delivery */
    private String deliveryOtp;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

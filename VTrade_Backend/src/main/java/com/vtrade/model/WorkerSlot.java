package com.vtrade.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "worker_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workerId;

    /** mon | tue | wed | thu | fri | sat | sun */
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    /** "HH:mm" */
    @Column(nullable = false)
    private String startTime;

    /** "HH:mm" */
    @Column(nullable = false)
    private String endTime;
}

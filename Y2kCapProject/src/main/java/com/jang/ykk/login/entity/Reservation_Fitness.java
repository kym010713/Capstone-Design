package com.jang.ykk.login.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "CAP_FITNESS_CENTER")
public class Reservation_Fitness {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fitness_seq")
    @SequenceGenerator(name = "fitness_seq", sequenceName = "CAP_FITNESS_CENTER_SEQ", allocationSize = 1)
    private Long id;

    private String location;

    @Column(name = "RESERVATION_START_DATE")
    private LocalDate reservationStartDate;

    private int months;
    
    @Column(name = "USER_ID") // 사용자 ID 컬럼 추가
    private Long userId;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Reservation_Fitness() {
    }

    public Reservation_Fitness(String location, LocalDate reservationStartDate, int months) {
        this.location = location;
        this.reservationStartDate = reservationStartDate;
        this.months = months;
    }
}

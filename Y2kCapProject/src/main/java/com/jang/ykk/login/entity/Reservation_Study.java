package com.jang.ykk.login.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CAP_STUDYROOM")
public class Reservation_Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @Column(name = "RESERVATION_DATE")
    private LocalDate reservationDate;

    @Column(name = "SEAT_NUMBER")
    private int seatNumber;

    @Column(name = "ENTRY_TIME")
    private String entryTime;

    @Column(name = "EXIT_TIME")
    private String exitTime;
    
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

    public Reservation_Study() {
    }

    public Reservation_Study(String location, LocalDate reservationDate, int seatNumber, String entryTime, String exitTime, Long userId) {
        this.location = location;
        this.reservationDate = reservationDate;
        this.seatNumber = seatNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.userId = userId;
    }

}

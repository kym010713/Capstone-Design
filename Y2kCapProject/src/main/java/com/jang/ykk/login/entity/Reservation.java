package com.jang.ykk.login.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.jang.ykk.login.model.ReservationId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(ReservationId.class)
public class Reservation {
    @Id
    private String location;

    @Id
    private LocalDate reservationDate;

    @Id
    private LocalTime reservationTime;

    public Reservation() {
    }

    public Reservation(String location, LocalDate reservationDate, LocalTime reservationTime) {
        this.location = location;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }

}

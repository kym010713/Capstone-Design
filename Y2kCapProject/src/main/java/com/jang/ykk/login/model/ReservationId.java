package com.jang.ykk.login.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ReservationId implements Serializable {
    private String location;
    private LocalDate reservationDate;
    private LocalTime reservationTime;

    public ReservationId() {
    }

    public ReservationId(String location, LocalDate reservationDate, LocalTime reservationTime) {
        this.location = location;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }

    // Getters, Setters, equals, and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationId that = (ReservationId) o;
        return Objects.equals(location, that.location) &&
               Objects.equals(reservationDate, that.reservationDate) &&
               Objects.equals(reservationTime, that.reservationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, reservationDate, reservationTime);
    }
}

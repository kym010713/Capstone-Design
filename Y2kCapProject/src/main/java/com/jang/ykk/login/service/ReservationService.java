package com.jang.ykk.login.service;

import org.springframework.stereotype.Service;

import com.jang.ykk.login.entity.Reservation;
import com.jang.ykk.login.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void createReservation(String location, LocalDate date, LocalTime time) {
        Reservation reservation = new Reservation(location, date, time);
        reservationRepository.save(reservation);
    }
}

package com.jang.ykk.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.Reservation;
import com.jang.ykk.login.model.ReservationId;

public interface ReservationRepository extends JpaRepository<Reservation, ReservationId> {
}

package com.jang.ykk.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.Reservation_Study;

public interface ReservationRepository_Study extends JpaRepository<Reservation_Study, Long> {
	List<Reservation_Study> findByUserId(Long userId);
}

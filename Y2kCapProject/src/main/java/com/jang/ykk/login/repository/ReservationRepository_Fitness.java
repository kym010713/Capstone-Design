package com.jang.ykk.login.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jang.ykk.login.entity.Reservation_Fitness;

public interface ReservationRepository_Fitness extends JpaRepository<Reservation_Fitness, Long> {
	List<Reservation_Fitness> findByUserId(Long userId);
}

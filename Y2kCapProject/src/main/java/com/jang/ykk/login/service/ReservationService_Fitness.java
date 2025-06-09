package com.jang.ykk.login.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jang.ykk.login.entity.Reservation_Fitness;
import com.jang.ykk.login.repository.ReservationRepository_Fitness;

@Service
public class ReservationService_Fitness {

    private final ReservationRepository_Fitness reservationRepository;
    private final EventService eventService; // 캘린더 이벤트 서비스

    public ReservationService_Fitness(ReservationRepository_Fitness reservationRepository, EventService eventService) {
        this.reservationRepository = reservationRepository;
        this.eventService = eventService;
        
    }

    @Transactional
    public void createReservation(String location, LocalDate startDate, int months, Long userId) {
        Reservation_Fitness reservation = new Reservation_Fitness(location, startDate, months);
        reservation.setUserId(userId); // 예약 시 사용자 ID 설정
        reservationRepository.save(reservation);

        // 캘린더에 시작 날짜 이벤트 추가
        String simpleTitleStart = "* 헬스장 이용 *"; // 제목에서는 <br> 제거된 버전 사용
        eventService.addResidentEvent(userId, startDate, simpleTitleStart, "이용 개월 수: " + months + "개월");

        // 이용 종료 날짜 계산
        LocalDate endDate = startDate.plusMonths(months);

        // 캘린더에 종료 날짜 이벤트 추가
        String simpleTitleEnd = "* 헬스장 이용 마감 *"; // 제목에서는 <br> 제거된 버전 사용
        eventService.addResidentEvent(userId, endDate, simpleTitleEnd, "이용 개월 수: " + months + "개월");
        
    }
    
 // 사용자별 예약 조회
    public List<Reservation_Fitness> findReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
    
 // 특정 예약 조회
    public Reservation_Fitness findReservationById(Long reservationId, Long userId) {
        Reservation_Fitness reservation = reservationRepository.findById(reservationId).orElse(null);
        // 예약이 있고, 해당 예약의 사용자 ID가 현재 사용자 ID와 일치하는지 확인
        if (reservation != null && reservation.getUserId().equals(userId)) {
            return reservation;
        }
        return null; // 일치하지 않으면 null 반환
    }

    // 예약 삭제
    @Transactional
    public boolean deleteReservation(Long reservationId, Long userId) {
        Reservation_Fitness reservation = findReservationById(reservationId, userId);
        if (reservation != null) {
            reservationRepository.delete(reservation);
            return true;
        }
        return false;
    }
    
    
}

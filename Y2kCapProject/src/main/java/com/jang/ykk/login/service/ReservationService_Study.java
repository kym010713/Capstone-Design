package com.jang.ykk.login.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jang.ykk.login.entity.Reservation_Study;
import com.jang.ykk.login.repository.ReservationRepository_Study;

@Service
public class ReservationService_Study {

    private final ReservationRepository_Study reservationRepository;
    private final EventService eventService; // 캘린더 이벤트 서비스

    public ReservationService_Study(ReservationRepository_Study reservationRepository, EventService eventService) {
        this.reservationRepository = reservationRepository;
        this.eventService = eventService; // eventService 초기화
    }


    @Transactional
    public void createReservation(Reservation_Study reservation) {
        // 독서실 예약 생성
        reservationRepository.save(reservation);

        // 캘린더에 사용자별 이벤트 추가 - 제목에서 <br> 제거
        String simpleTitle = "* 독서실 이용 *";
        String description = "입실 시간: " + reservation.getEntryTime() + ":00" + "<br>퇴실 시간: " + reservation.getExitTime() + ":00" + "<br>좌석 번호: " + reservation.getSeatNumber();
        
        // userId를 포함하여 eventService에 이벤트 추가
        eventService.addResidentEvent(
            reservation.getUserId(), // userId 추가
            reservation.getReservationDate(),
            simpleTitle, // 간단한 제목
            description  // 줄바꿈 처리된 설명
        );
    }

    
    // 사용자별 예약 조회
    public List<Reservation_Study> findReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // 특정 예약 조회
    public Reservation_Study findReservationById(Long reservationId, Long userId) {
        Reservation_Study reservation = reservationRepository.findById(reservationId).orElse(null);
        // 예약이 있고, 해당 예약의 사용자 ID가 현재 사용자 ID와 일치하는지 확인
        if (reservation != null && reservation.getUserId().equals(userId)) {
            return reservation;
        }
        return null; // 일치하지 않으면 null 반환
    }

    // 예약 삭제
    @Transactional
    public boolean deleteReservation(Long reservationId, Long userId) {
        Reservation_Study reservation = findReservationById(reservationId, userId);
        if (reservation != null) {
            reservationRepository.delete(reservation);
            return true;
        }
        return false;
    }
}

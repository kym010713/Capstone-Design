package com.jang.ykk.login.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jang.ykk.login.entity.SharedEvent;
import com.jang.ykk.login.repository.SharedEventRepository;

@Service
public class AdminCalendarService {

    private final SharedEventRepository sharedEventRepository;

    @Autowired
    public AdminCalendarService(SharedEventRepository sharedEventRepository) {
        this.sharedEventRepository = sharedEventRepository;
    }

    public SharedEvent addAdminEvent(LocalDate eventDate, String title, String description) {
        // 새로운 SharedEvent 객체 생성
        SharedEvent sharedEvent = new SharedEvent();
        
        // 이벤트 날짜, 제목, 설명 설정
        sharedEvent.setEventDate(eventDate.atStartOfDay());
        sharedEvent.setTitle(title);
        sharedEvent.setDescription(description);

        // 삭제 여부와 생성/수정 시간 설정
        sharedEvent.setDelYn("N"); // 삭제되지 않은 상태로 설정
        sharedEvent.setCreatedAt(LocalDateTime.now());
        sharedEvent.setUpdatedAt(LocalDateTime.now());

        // 리포지토리에 이벤트 저장
        return sharedEventRepository.save(sharedEvent);
    }


    public boolean updateEvent(Long id, LocalDate eventDate, String title, String description) {
        SharedEvent existingEvent = sharedEventRepository.findById(id).orElse(null);
        if (existingEvent != null) {
            existingEvent.setEventDate(eventDate.atStartOfDay());
            existingEvent.setTitle(title);
            existingEvent.setDescription(description);
            existingEvent.setUpdatedAt(LocalDateTime.now());
            sharedEventRepository.save(existingEvent);
            return true;
        }
        return false;
    }


    public boolean deleteEvent(Long id) {
        if (sharedEventRepository.existsById(id)) {
            sharedEventRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public List<SharedEvent> getAllSharedEvents() {
        return sharedEventRepository.findAll();
    }

    public List<SharedEvent> findEventsByDate(LocalDate date) {
        return sharedEventRepository.findByEventDate(date.atStartOfDay());
    }
    
    public SharedEvent findEventById(Long id) {
        return sharedEventRepository.findById(id).orElse(null);
    }

    
	/*
	 * public List<List<LocalDate>> generateCalendarDays(int year, int month) {
	 * List<List<LocalDate>> calendarDays = new ArrayList<>(); LocalDate
	 * firstDayOfMonth = LocalDate.of(year, month, 1); LocalDate firstDayOfCalendar
	 * = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue());
	 * 
	 * LocalDate day = firstDayOfCalendar; for (int i = 0; i < 6; i++) {
	 * List<LocalDate> week = new ArrayList<>(); for (int j = 0; j < 7; j++) {
	 * week.add(day); day = day.plusDays(1); } calendarDays.add(week); } return
	 * calendarDays; }
	 */
    


}
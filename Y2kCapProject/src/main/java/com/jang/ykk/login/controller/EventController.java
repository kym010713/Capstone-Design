package com.jang.ykk.login.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jang.ykk.login.entity.SharedEvent;
import com.jang.ykk.login.model.Event;
import com.jang.ykk.login.service.AdminCalendarService;
import com.jang.ykk.login.service.EventService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EventController {

    private final EventService eventService;
    private final AdminCalendarService adminCalendarService;

    @Autowired
    public EventController(EventService eventService, AdminCalendarService adminCalendarService) {
        this.eventService = eventService;
        this.adminCalendarService = adminCalendarService;
    }

    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(value = "year", required = false) Integer year,
                               @RequestParam(value = "month", required = false) Integer month,
                               Model model,
                               HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null && (role == null || !role.equals("ADMIN"))) {
            return "redirect:" + (role != null && role.equals("ADMIN") ? "/admin/login" : "/residents/login");
        }

        LocalDate today = LocalDate.now();
        if (year == null) year = today.getYear();
        if (month == null) month = today.getMonthValue();

        if (month < 1) {
            year -= 1;
            month = 12;
        } else if (month > 12) {
            year += 1;
            month = 1;
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        List<Event> events = (userId != null) ? eventService.findByMonthAndUserId(userId, year, month) : new ArrayList<>();
        Map<LocalDate, List<String>> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(Event::getEventDate,
                		 Collectors.mapping(Event::getTitle, Collectors.toList())));
        
        // 공동일정
		List<SharedEvent> sharedEvents = adminCalendarService.getAllSharedEvents();
		        
		Map<LocalDate, List<String>> sharedEventsByDate = sharedEvents.stream()
				.collect(Collectors.groupingBy(
		        	       event -> event.getEventDate().toLocalDate(),
		        	     Collectors.mapping(SharedEvent::getTitle,
		        	     Collectors.toList())
		        	    ));

        List<List<LocalDate>> calendarDays = generateCalendarDays(firstDayOfMonth, lastDayOfMonth);

        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("eventsByDate", eventsByDate);
        model.addAttribute("sharedEventsByDate", sharedEventsByDate);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("userId", userId);
        model.addAttribute("years", IntStream.rangeClosed(today.getYear() - 5, today.getYear() + 5).boxed().collect(Collectors.toList()));
        model.addAttribute("months", IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList()));

        return "calendar/calendar";
    }

    @GetMapping("/event/getEvents")
    public String getEvents(@RequestParam("date") String date,
                            HttpSession session, 
                            Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null && (role == null || !role.equals("ADMIN"))) {
            return "redirect:" + (role != null && role.equals("ADMIN") ? "/admin/login" : "/residents/login");
        }

        LocalDate selectedDate = LocalDate.parse(date);

        // 공동 일정 가져오기
        List<SharedEvent> sharedEvents = adminCalendarService.findEventsByDate(selectedDate);
        List<Event> events;

        if ("ADMIN".equals(role)) {
            events = convertSharedEventsToEvents(sharedEvents);
        } else {
            // 입주민용 일정과 공동 일정 통합
            events = eventService.findByDateAndUserId(selectedDate, userId);
        }

        // 공동 일정과 개인 일정 모두 모델에 추가
        model.addAttribute("events", events);
        model.addAttribute("sharedEvents", sharedEvents);

        return "event/eventList";
    }


 // 입주민용 일정 추가
    @PostMapping("/event/addEvent")
    @ResponseBody
    public String addResidentEvent(@RequestParam("eventDate") LocalDate eventDate,
                                   @RequestParam("title") String title,
                                   @RequestParam("description") String description,
                                   HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        eventService.addResidentEvent(userId, eventDate, title, description);
        return "입주민 일정 등록 완료";
    }

    

    @PostMapping("/event/updateEvent")
    @ResponseBody
    public String updateEvent(@RequestParam("id") Long id,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description,
                              HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        boolean success = eventService.updateEvent(userId, id, title, description);

        return success ? "수정 완료" : "수정 실패: 해당 일정이 존재하지 않거나 권한이 없습니다.";
    }


    @PostMapping("/event/deleteEvent")
    @ResponseBody
    public String deleteEvent(@RequestParam("id") Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        boolean success = eventService.deleteEvent(userId, id);

        return success ? "삭제 완료" : "삭제 실패: 해당 일정이 존재하지 않거나 권한이 없습니다.";
    }

   

    private List<List<LocalDate>> generateCalendarDays(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        List<List<LocalDate>> calendarDays = new ArrayList<>();
        LocalDate firstDayOfWeek = firstDayOfMonth.withDayOfMonth(1).minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate lastDayOfWeek = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue() % 7);

        for (LocalDate date = firstDayOfWeek; !date.isAfter(lastDayOfWeek); date = date.plusDays(7)) {
            List<LocalDate> week = IntStream.range(0, 7)
                    .mapToObj(date::plusDays)
                    .collect(Collectors.toList());
            calendarDays.add(week);
        }

        return calendarDays;
    }

    private List<Event> convertSharedEventsToEvents(List<SharedEvent> sharedEvents) {
        return sharedEvents.stream()
            .map(sharedEvent -> new Event(
                sharedEvent.getId(),
                sharedEvent.getEventDate().toLocalDate(),
                sharedEvent.getTitle(),
                sharedEvent.getDescription(),
                "n",
                null
            ))
            .collect(Collectors.toList());
    }
}
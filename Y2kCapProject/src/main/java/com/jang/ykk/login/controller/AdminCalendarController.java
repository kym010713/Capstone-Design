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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jang.ykk.login.entity.SharedEvent;
import com.jang.ykk.login.service.AdminCalendarService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/calendar")
public class AdminCalendarController {

	private final AdminCalendarService adminCalendarService;

	@Autowired
	public AdminCalendarController(AdminCalendarService adminCalendarService) {
		this.adminCalendarService = adminCalendarService;
	}

	@GetMapping
	public String showAdminCalendar(@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "month", required = false) Integer month, Model model, HttpSession session) {
		LocalDate today = LocalDate.now();
		if (year == null)
			year = today.getYear();
		if (month == null)
			month = today.getMonthValue();

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

		List<SharedEvent> sharedEvents = adminCalendarService.getAllSharedEvents();

		Map<LocalDate, List<String>> sharedEventsByDate = sharedEvents.stream()
				.collect(Collectors.groupingBy(event -> event.getEventDate().toLocalDate(),
						Collectors.mapping(SharedEvent::getTitle, Collectors.toList())));

		List<List<LocalDate>> calendarDays = generateCalendarDays(firstDayOfMonth, lastDayOfMonth);

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("calendarDays", calendarDays);
		model.addAttribute("sharedEventsByDate", sharedEventsByDate);

		return "admin/calendar";
	}

	private List<List<LocalDate>> generateCalendarDays(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
		List<List<LocalDate>> calendarDays = new ArrayList<>();

		LocalDate firstDayOfWeek = firstDayOfMonth.withDayOfMonth(1)
				.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
		LocalDate lastDayOfWeek = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue() % 7);

		for (LocalDate date = firstDayOfWeek; !date.isAfter(lastDayOfWeek); date = date.plusDays(7)) {
			List<LocalDate> week = IntStream.range(0, 7).mapToObj(date::plusDays).collect(Collectors.toList());
			calendarDays.add(week);
		}

		return calendarDays;
	}

	@PostMapping("/addSharedEvent")
	@ResponseBody
	public String addSharedEvent(@RequestParam("eventDate") LocalDate eventDate, @RequestParam("title") String title,
			@RequestParam("description") String description, HttpSession session) {
		/*
		 * String role = (String) session.getAttribute("role");
		 * 
		 * 
		 * if (!"ADMIN".equals(role)) { return "권한이 없습니다."; }
		 */

		// addAdminEvent 메서드를 개별 인자로 호출
		adminCalendarService.addAdminEvent(eventDate, title, description);

		return "공동 일정 등록 완료";
	}


	@PostMapping("/updateSharedEvent")
    @ResponseBody
    public String updateSharedEvent(@RequestParam("id") Long id,
                                    @RequestParam("eventDate") LocalDate eventDate,
                                    @RequestParam("title") String title,
                                    @RequestParam("description") String description) {
        boolean success = adminCalendarService.updateEvent(id, eventDate, title, description);
        return success ? "수정 완료" : "수정 실패: 해당 일정이 존재하지 않습니다.";
    }

    @PostMapping("/deleteSharedEvent")
    @ResponseBody
    public String deleteSharedEvent(@RequestParam("id") Long id) {
        boolean success = adminCalendarService.deleteEvent(id);
        return success ? "공동 일정 삭제 완료" : "삭제 실패: 해당 일정이 존재하지 않습니다.";
    }
    
    @GetMapping("/getEvents")
    public String getEvents(@RequestParam("date") String date, Model model) {
        LocalDate selectedDate = LocalDate.parse(date);
        List<SharedEvent> sharedEvents = adminCalendarService.findEventsByDate(selectedDate);
        model.addAttribute("sharedEvents", sharedEvents);
        return "admin/eventList";
    }

	
}
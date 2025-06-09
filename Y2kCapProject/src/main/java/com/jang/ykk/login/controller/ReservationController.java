package com.jang.ykk.login.controller;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jang.ykk.login.entity.Reservation_Study;
import com.jang.ykk.login.service.ReservationService_Fitness;
import com.jang.ykk.login.service.ReservationService_Study;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReservationController {
    private final ReservationService_Fitness fitnessService;
    private final ReservationService_Study studyService;

    public ReservationController(ReservationService_Fitness fitnessService, ReservationService_Study studyService) {
        this.fitnessService = fitnessService;
        this.studyService = studyService;
    }

    // 커뮤니티 센터 페이지
    @GetMapping(value = "/resident/community")
    public String communityCenter() {
        return "Community/community";
    }

    // 피트니스 센터 페이지 (달력 포함)
    @GetMapping(value = "/resident/communityCenter/fitnessCenter")
    public String fitnessCenter(@RequestParam(value = "year", required = false) Integer year,
                                @RequestParam(value = "month", required = false) Integer month, Model model) {
        YearMonth yearMonth = getCurrentOrSpecifiedMonth(year, month);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("prevMonth", yearMonth.minusMonths(1));
        model.addAttribute("nextMonth", yearMonth.plusMonths(1));

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        firstDayOfWeek = firstDayOfWeek == 0 ? 7 : firstDayOfWeek;

        model.addAttribute("daysInMonth", firstDayOfMonth.datesUntil(lastDayOfMonth.plusDays(1)).toList());
        model.addAttribute("firstDayOfWeek", firstDayOfWeek);

        return "Community/FitnessCenter";
    }

    // 피트니스 센터 예약 처리 및 확인 페이지로 이동
    @PostMapping("/resident/communityCenter/fitnessCenter/reserve")
    public String reserveFitnessCenter(@RequestParam("location") String location,
                                       @RequestParam("startDate") String startDateStr,
                                       @RequestParam("months") int months,
                                       HttpSession session,
                                       Model model) {
    	Long userId = (Long) session.getAttribute("userId");
        LocalDate startDate = LocalDate.parse(startDateStr);
        fitnessService.createReservation(location, startDate, months, userId);

        model.addAttribute("location", location);
        model.addAttribute("startDate", startDate);
        model.addAttribute("months", months);

        return "Community/FitnessCenterConfirmation";
    }

    // 독서실 페이지 (달력 포함)
    @GetMapping(value = "/resident/communityCenter/studyRoom")
    public String studyRoom(@RequestParam(value = "year", required = false) Integer year,
                            @RequestParam(value = "month", required = false) Integer month, Model model) {
        YearMonth yearMonth = getCurrentOrSpecifiedMonth(year, month);
        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("prevMonth", yearMonth.minusMonths(1));
        model.addAttribute("nextMonth", yearMonth.plusMonths(1));

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        firstDayOfWeek = firstDayOfWeek == 0 ? 7 : firstDayOfWeek;

        model.addAttribute("daysInMonth", firstDayOfMonth.datesUntil(lastDayOfMonth.plusDays(1)).toList());
        model.addAttribute("firstDayOfWeek", firstDayOfWeek);

        return "Community/StudyRoom";
    }

    // 독서실 예약 처리 및 확인 페이지로 이동
    @PostMapping("/resident/communityCenter/studyRoom/reserve")
    public String reserveStudyRoom(@RequestParam("location") String location,
                                   @RequestParam("date") String dateStr,
                                   @RequestParam("seatNumber") int seatNumber,
                                   @RequestParam("entryTime") String entryTime,
                                   @RequestParam("exitTime") String exitTime,
                                   HttpSession session,
                                   Model model) {
    	Long userId = (Long) session.getAttribute("userId");
        LocalDate reservationDate = LocalDate.parse(dateStr);
        Reservation_Study reservation = new Reservation_Study(location, reservationDate, seatNumber, entryTime, exitTime, userId);
        studyService.createReservation(reservation);

        // 예약 확인 정보를 모델에 추가
        model.addAttribute("location", location);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("seatNumber", seatNumber);
        model.addAttribute("entryTime", entryTime);
        model.addAttribute("exitTime", exitTime);

        return "Community/StudyRoomConfirmation";
    }

    private YearMonth getCurrentOrSpecifiedMonth(Integer year, Integer month) {
        LocalDate today = LocalDate.now();
        if (year == null) year = today.getYear();
        if (month == null) month = today.getMonthValue();
        return YearMonth.of(year, month);
    }
}

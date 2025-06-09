package com.jang.ykk.login.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jang.ykk.login.entity.Resident;
import com.jang.ykk.login.service.ResidentService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/residents")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    // 회원가입 페이지를 보여주는 엔드포인트
    @GetMapping("/register")
    public String showRegisterForm() {
        return "login/register"; // register.html을 렌더링
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login/login"; // login.html을 렌더링
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/"; // 로그아웃 후 메인 페이지로 리다이렉트
    }

    // 로그인 처리 로직
    @PostMapping("/login")
    public String login(@RequestParam String userid, @RequestParam String password, HttpSession session, Model model) {
        Optional<Resident> resident = residentService.login(userid, password);

        if (resident.isPresent() && resident.get().getApprovalStatus().equals("Y")) {
            // 로그인 성공 시 세션에 사용자 정보 저장
        	session.setAttribute("userId", resident.get().getId()); // userId를 세션에 저장
            session.setAttribute("username", resident.get().getName());
            session.setAttribute("buildingNumber", resident.get().getBuildingNumber());
            session.setAttribute("unitNumber", resident.get().getUnitNumber());
            session.setAttribute("nickname", resident.get().getNickname()); // 닉네임 추가
            session.setAttribute("role", "RESIDENT"); // 입주민 역할 설정
            
            // 세션에 값이 잘 저장되었는지 로그로 확인
            System.out.println("닉네임 저장됨: " + resident.get().getNickname());
            System.out.println("Building Number 저장됨: " + resident.get().getBuildingNumber());
            System.out.println("Unit Number 저장됨: " + resident.get().getUnitNumber());

            return "redirect:/"; // 로그인 성공 시 메인 페이지로 리다이렉트
        } else {
            model.addAttribute("error", "로그인 실패 또는 승인 대기 중입니다.");
            return "login/login"; // 로그인 실패 시 로그인 페이지로 이동
        }
    }

    // 승인 대기 중인 사용자 및 승인 완료된 사용자 목록을 보여주는 메서드
    @GetMapping("/adminApproval")
    public String showAdminApproval(Model model) {
        model.addAttribute("pendingResidents", residentService.findPendingResidents()); // 승인 대기 사용자 목록
        model.addAttribute("approvedResidents", residentService.findApprovedResidents()); // 승인 완료 사용자 목록
        return "login/adminApproval"; // adminApproval.html을 렌더링
    }

    // 사용자 승인 처리 메서드
    @PostMapping("/approve")
    public String approveResident(@RequestParam Long residentId, RedirectAttributes redirectAttributes) {
        residentService.approveResident(residentId);
        redirectAttributes.addFlashAttribute("approvalMessage", "승인 완료");
        return "redirect:/residents/adminApproval"; // 승인 후 목록 페이지로 리다이렉트
    }
    
 // 승인 완료된 사용자 삭제 요청 처리
    @PostMapping("/delete")
    public String deleteApprovedResident(@RequestParam Long residentId, RedirectAttributes redirectAttributes) {
        residentService.deleteApprovedResident(residentId);
        redirectAttributes.addFlashAttribute("deleteMessage", "승인 완료된 사용자가 삭제되었습니다.");
        return "redirect:/residents/adminApproval"; // 삭제 후 목록 페이지로 리다이렉트
    }

    // 회원가입 처리 로직
    @PostMapping("/register")
    public String registerResident(
            @RequestParam("role") String role,
            @RequestParam("apartmentName") String apartmentName,
            @RequestParam("buildingNumber") String buildingNumber,
            @RequestParam("unitNumber") String unitNumber,
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("userid") String userid,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone,
            @RequestParam(value = "headOfHousehold", required = false) String headOfHousehold,
            @RequestParam(value = "approvalCode", required = false) String approvalCode,
            RedirectAttributes redirectAttributes) {

        Resident resident = new Resident();
        resident.setRole(role);
        resident.setApartmentName(apartmentName);
        resident.setBuildingNumber(buildingNumber);
        resident.setUnitNumber(unitNumber);
        resident.setName(name);
        resident.setNickname(nickname);
        resident.setUserid(userid);
        resident.setPassword(password);
        resident.setPhoneNumber(phone);

        LocalDateTime nowDate = LocalDateTime.now();
        resident.setCreatedAt(nowDate);
        resident.setUpdatedAt(nowDate);

        // 세대원인 경우 세대주 정보 확인 및 자동 승인 로직
        if ("household".equals(role)) {
            if (headOfHousehold != null && approvalCode != null) {
                // 세대주 정보 확인
                if (residentService.verifyHeadOfHouseholdMatch(apartmentName, buildingNumber, unitNumber, headOfHousehold, approvalCode)) {
                    resident.setApprovalStatus("Y"); // 일치하면 승인 상태를 'Y'로 설정
                    resident.setHeadOfHousehold(headOfHousehold); // 세대주 이름 저장
                    resident.setApprovalCode(approvalCode); // 승인 코드 저장
                } else {
                    resident.setApprovalStatus("N"); // 일치하지 않으면 승인 대기로 설정
                    redirectAttributes.addFlashAttribute("error", "세대주 이름 또는 승인 코드가 잘못되었습니다.");
                    return "redirect:/residents/register"; // 실패 시 회원가입 페이지로 리다이렉트
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "세대주와 승인 코드를 입력해야 합니다.");
                return "redirect:/residents/register";
            }
        }


        // 세대주인 경우 승인 코드 생성 로직
        if ("head".equals(role)) {
            resident.setApprovalCode(generateApprovalCode()); // 승인 코드 생성
        }

        residentService.saveResident(resident);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/"; // 회원가입 완료 후 페이지 리다이렉트
    }

    // 승인 코드 생성 메서드
    private String generateApprovalCode() {
        // 승인 코드 생성 로직 (예: 현재 날짜와 랜덤 6자리 수 조합)
        return "202410" + String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}

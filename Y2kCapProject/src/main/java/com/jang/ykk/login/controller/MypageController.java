package com.jang.ykk.login.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jang.ykk.login.entity.CapBoard;
import com.jang.ykk.login.entity.Resident;
import com.jang.ykk.login.repository.CapBoardRepository;
import com.jang.ykk.login.service.ResidentService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    @Autowired
    private CapBoardRepository capBoardRepository;

    @Autowired
    private ResidentService residentService;

    // 회원 정보 업데이트 처리
    @PostMapping("/updateProfile")
    public String updateProfile(
            @RequestParam("nickname") String nickname,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long residentId = (Long) session.getAttribute("userId");
        Optional<Resident> residentOpt = residentService.findById(residentId);

        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            resident.setNickname(nickname);
            resident.setPhoneNumber(phone);
            
            // 비밀번호 설정
            if (password != null && !password.isEmpty()) {
                resident.setPassword(password); // 비밀번호 암호화가 필요한 경우, 여기서 암호화 처리
            }

            residentService.saveResident(resident);

            // 세션에 새로운 닉네임을 업데이트
            session.setAttribute("nickname", nickname);
            redirectAttributes.addFlashAttribute("message", "회원 정보가 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "회원 정보를 찾을 수 없습니다.");
        }

        return "redirect:/mypage"; // 수정 완료 후 다시 마이페이지로 리다이렉트
    }

    // 마이페이지 화면을 보여주는 메서드
    @GetMapping
    public String showMyPage(HttpSession session, Model model) {
        Long residentId = (Long) session.getAttribute("userId");

        // 디버그용 로그 추가
        System.out.println("Resident ID: " + residentId);

        Optional<Resident> residentOpt = residentService.findById(residentId);
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            model.addAttribute("resident", resident);

            // 세대주 및 세대원 정보 가져오기
            Resident headOfHousehold = residentService.findHeadOfHouseholdByResidentId(residentId);
            List<Resident> householdMembers = residentService.findHouseholdMembersByResidentId(residentId);
            model.addAttribute("headOfHousehold", headOfHousehold);
            model.addAttribute("householdMembers", householdMembers);

            // 게시글 목록 추가
            String author = resident.getNickname() + " (" + resident.getBuildingNumber() + ")";
            List<CapBoard> boards = capBoardRepository.findByAuthor(author);
            model.addAttribute("boards", boards);

            // 디버그용 로그
            System.out.println("Resident: " + resident.getNickname());
            System.out.println("게시글 수: " + boards.size());
        } else {
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
        }

        return "mypage/mypage"; // mypage.html로 이동
    }
}

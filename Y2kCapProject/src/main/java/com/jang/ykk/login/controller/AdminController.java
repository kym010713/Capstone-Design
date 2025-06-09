package com.jang.ykk.login.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jang.ykk.login.entity.Admin;
import com.jang.ykk.login.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/login")
    public String showAdminLoginForm() {
        return "login/adminLogin"; // 관리자 로그인 페이지
    }

    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<Admin> admin = adminService.login(username, password);

        if (admin.isPresent()) {
            // 로그인 성공 시 세션에 관리자 정보와 역할(role)을 저장
            session.setAttribute("adminUsername", admin.get().getUsername());
            session.setAttribute("adminName", admin.get().getName());
            session.setAttribute("username", admin.get().getUsername()); // 추가
            session.setAttribute("role", "ADMIN"); // 역할을 "ADMIN"으로 저장
            System.out.println("관리자 로그인 성공: " + session.getAttribute("username") + ", 역할: " + session.getAttribute("role"));
            return "redirect:/admin/dashboard"; // 관리자 대시보드로 리다이렉트
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login/adminLogin"; // 로그인 실패 시 다시 로그인 페이지로 이동
        }
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/"; // 로그아웃 후 메인 페이지로 리다이렉트
    }

    
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        
        model.addAttribute("username", username);
        model.addAttribute("role", role);

        return "admin/dashboard"; 
    }



}
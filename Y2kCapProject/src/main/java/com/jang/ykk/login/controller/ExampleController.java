package com.jang.ykk.login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

public class ExampleController {
	@GetMapping("/protectedPage")
    public String protectedPage(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("username") == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/residents/login"; // 로그인 페이지로 리다이렉트
        }
        return "protectedPage"; // 로그인이 되어 있으면 해당 페이지로 이동
    }

}

package com.jang.ykk.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PhoneController {
	 
	@GetMapping("/phonebook")
	    public String showphonebookForm() {
	        return "phonebook/phonebook"; 
	    }
}
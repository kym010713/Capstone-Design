package com.jang.ykk.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {
	
	@GetMapping("/map")
    public String showmapForm() {
        return "map/map"; 
    }

}

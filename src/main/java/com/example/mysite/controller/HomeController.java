package com.example.mysite.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping(value = "/")
	public String home(Model model) {
		System.out.println("/ 요청");
		LocalDateTime now = LocalDateTime.now();
		Date dateNow = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		model.addAttribute("now", dateNow);
		return "home";
	}
}









package com.example.mysite.controller;

import com.example.mysite.dto.User;
import com.example.mysite.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/register")
	public String registerForm() {
		return "user/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("user") User user) {
		userService.register(user);
		return "redirect:/user/login";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "user/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session, Model model) {
		User user = userService.login(username, password);
		if (user != null) {
			session.setAttribute("loginUser", user);
			return "redirect:/board/list";
		} else {
			model.addAttribute("loginError", true);
			return "user/login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/user/login";
	}
}

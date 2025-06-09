package com.example.mysite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mysite.dao.UserDao;
import com.example.mysite.dto.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	// 회원가입
	public void register(User user) {
		if (userDao.existsByUsername(user.getUsername())) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}
		userDao.insert(user);
	}

	// 로그인
	public User login(String username, String password) {
		try {
			User user = userDao.findByUsername(username);
			if (user.getPassword().equals(password)) {
				return user;
			}
		} catch (Exception e) {
			// 사용자 없음 또는 DB 에러
		}
		return null;
	}
}

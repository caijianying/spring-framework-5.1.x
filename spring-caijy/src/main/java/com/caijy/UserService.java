package com.caijy;

import com.caijy.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserService {
	@Autowired
	UserMapper userMapper;

	public Map getUser(){
		return userMapper.getUser("7");
	}
}


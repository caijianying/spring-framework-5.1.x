package com.caijy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class X {

	@Autowired
	Y y;

	public X() {
		System.out.println("X created!");
	}

	public void invoke() {
		System.out.println("x invoke!");
	}
}

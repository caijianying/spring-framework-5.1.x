package com.caijy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Z  implements ApplicationContextAware {

	@Autowired
	X x;

	//回调方法
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("ApplicationContextAware callback!");
	}

	//生命周期初始化回调方法 spring 完成属性注入(比如注入了X)  如果没有需要注入的属性 则不会调用这个方法
	//只有调用这个方法了 才是一个spring bean
	@PostConstruct
	public void zinit(){
		System.out.println("call z lifecycle init callback !");
	}
}

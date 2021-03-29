package com.caijy.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

//加上此注解 该类对自动注入的操作生效
//@Component
public class CancleAutowired implements InstantiationAwareBeanPostProcessor {

	//此方法返回false 会阻止bean的自动注入 扩展点...
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return false;
	}
}

package com.caijy.spring;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ClassMethodInterceptor implements MethodInterceptor {

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

		//实现代理逻辑
		System.out.println("inteceptor success!");

		//调用父类方法
//		return methodProxy.invokeSuper(o,objects);
		return null;
	}
}

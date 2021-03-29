package com.caijy.mybatisspring;

import java.lang.reflect.Proxy;

public class MySession {
	public static Object getProxy(Class clazz){
		return Proxy.newProxyInstance(MySession.class.getClassLoader(),new Class[]{clazz},new CaijInvocationHandler());
	}


}

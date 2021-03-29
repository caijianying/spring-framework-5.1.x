package com.caijy.mybatisspring;

import com.caijy.entity.User;
import org.apache.ibatis.annotations.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CaijInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("qryDB:" + method.getAnnotation(Select.class).value()[0]);
//		Object invoke = method.invoke(method.getDefaultValue(), args);
//		System.out.println(invoke);
		System.out.println(proxy.getClass());
		Map<String,String> map = new HashMap<>();
		map.put("name","小张");
		return map;
	}
}

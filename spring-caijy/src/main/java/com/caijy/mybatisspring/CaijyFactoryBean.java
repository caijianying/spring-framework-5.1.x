package com.caijy.mybatisspring;

import com.caijy.dao.UserMapper;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
public class CaijyFactoryBean extends SqlSessionDaoSupport implements FactoryBean {
	Class mapperInterface = UserMapper.class;

	@Override
	public Object getObject(){
		Class[] classes = new Class[]{mapperInterface};
		Object o = Proxy.newProxyInstance(mapperInterface.getClassLoader(), classes, new CaijInvocationHandler());
		return o;
	}

	@Override
	public Class<?> getObjectType() {
		return mapperInterface;
	}

}
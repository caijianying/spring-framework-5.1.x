package com.caijy.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

@Component
public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		GenericBeanDefinition gbd = (GenericBeanDefinition) beanFactory.getBeanDefinition("x");
		System.out.println(gbd.getAutowireMode());

		for (String definitionName : beanFactory.getBeanDefinitionNames()) {
			System.out.println(definitionName);
		}
		// 此时会报错
		// 因为此时还在执行invokeBeanFactoryPostProcessors 方法，
		// beanDefinitionMap中虽然有了这个实例 但是bean还没有实例化 gbd.getBeanClass()的返回的值还是一个字符串
		// 实例化的时候spring才会将string转成类对象
//		System.out.println(gbd.getBeanClass());
//		gbd.setBeanClass(Y.class);
	}

}

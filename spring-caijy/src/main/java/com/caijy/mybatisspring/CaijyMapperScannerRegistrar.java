package com.caijy.mybatisspring;

import com.caijy.annotation.CaijyMapperScan;
import com.caijy.dao.UserMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

public class CaijyMapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
	private ResourceLoader resourceLoader;
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		BeanDefinitionBuilder genericBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(CaijyFactoryBean.class);
		String[] definitionNames = registry.getBeanDefinitionNames();
		for (String defnitionName:definitionNames){
			genericBeanDefinition.addConstructorArgValue(defnitionName);
		}
//		AbstractBeanDefinition beanDefinition = genericBeanDefinition.getBeanDefinition();
//		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.caijy.dao.UserMapper");
//		beanDefinition.setBeanClass(CaijyFactoryBean.class);
//		registry.registerBeanDefinition(UserMapper.class.getSimpleName(),beanDefinition);
		AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(CaijyMapperScan.class.getName()));
		ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
		scanner.setSqlSessionTemplateBeanName(annoAttrs.getString("sqlSessionTemplateRef"));
		scanner.setSqlSessionFactoryBeanName(annoAttrs.getString("sqlSessionFactoryRef"));
		scanner.registerFilters();
		scanner.scan(importingClassMetadata.getAnnotationAttributes("com.caijy.annotation.CaijyMapperScan").get("value")+"");
		AbstractBeanDefinition beanDefinition = genericBeanDefinition.getBeanDefinition();
		beanDefinition.setBeanClass(CaijyFactoryBean.class);
		registry.registerBeanDefinition(UserMapper.class.getSimpleName(),beanDefinition);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}

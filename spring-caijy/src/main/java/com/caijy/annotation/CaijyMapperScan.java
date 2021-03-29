package com.caijy.annotation;

import com.caijy.mybatisspring.CaijyFactoryBean;
import com.caijy.mybatisspring.CaijyMapperScannerRegistrar;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CaijyMapperScannerRegistrar.class)
public @interface CaijyMapperScan {

	String[] value() default {};

	String[] basePackages() default {};

	String sqlSessionTemplateRef() default "";

	/**
	 * Specifies which {@code SqlSessionFactory} to use in the case that there is
	 * more than one in the spring context. Usually this is only needed when you
	 * have more than one datasource.
	 */
	String sqlSessionFactoryRef() default "";

	/**
	 * Specifies a custom MapperFactoryBean to return a mybatis proxy as spring bean.
	 *
	 */
	Class<? extends CaijyFactoryBean> factoryBean() default CaijyFactoryBean.class;
}

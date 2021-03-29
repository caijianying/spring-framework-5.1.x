/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/*
 * spring容器初始化bean的大概过程
 * 1：实例化一个ApplicationContext的对象；
 * 2：调用bean工厂后置处理器完成扫描；
 * 3：循环解析扫描出来的类信息；
 * 4：实例化一个BeanDefinition对象来存储解析出来的信息；
 * 5：把实例化好的beanDefinition对象put到beanDefinitionMap当中缓存起来，以便后面实例化bean；
 * 6：再次调用bean工厂后置处理器；
 * 7：当然spring还会干很多事情，比如国际化，比如注册BeanPostProcessor等等，如果我们只关心如何实例化一个bean的话那么这一步就是spring调用finishBeanFactoryInitialization方法来实例化单例的bean，实例化之前spring要做验证，需要遍历所有扫描出来的类，依次判断这个bean是否Lazy，是否prototype，是否abstract等等；
 * 8：如果验证完成spring在实例化一个bean之前需要推断构造方法，因为spring实例化对象是通过构造方法反射，故而需要知道用哪个构造方法；
 * 9：推断完构造方法之后spring调用构造方法反射实例化一个对象；注意我这里说的是对象、对象、对象；这个时候对象已经实例化出来了，但是并不是一个完整的bean，最简单的体现是这个时候实例化出来的对象属性是没有注入，所以不是一个完整的bean；
 * 10：spring处理合并后的beanDefinition(合并？是spring当中非常重要的一块内容，后面的文章我会分析)；
 * 11：判断是否支持循环依赖，如果支持则提前把一个工厂存入singletonFactories——map；
 * 12：判断是否需要完成属性注入
 * 13：如果需要完成属性注入，则开始注入属性
 * 14：判断bean的类型回调Aware接口
 * 15：调用生命周期回调方法
 * 16：如果需要代理则完成代理
 * 17：put到单例池——bean完成——存在spring容器当中

 * 原文链接：https://blog.csdn.net/java_lyvee/article/details/101793774
 */
//spring 容器对象-AnnotationConfigApplicationContext
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	/*
	 * @description
	 * 注解bean定义读取器，主要作用是用来读取被注解的了bean
	 *
	 * @datetime 2020-3-2 0002 21:03
	 */
	private final AnnotatedBeanDefinitionReader reader;

	/*
	 * @description
	 * 1.扫描器，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
	 * 2.可以用来扫描包和类，继而转换成beanDefinition，但是实际上我们扫描包的工作并不是这个scanner对象完成的
	 * 是spring自己new的一个ClassPathBeanDefinitionScanner
	 * 3.这里的scanner仅仅是为了我们能够在外部调用AnnotationConfigApplicationContext的scan方法
	 *
	 * @datetime 2020-3-2 0002 21:05
	 */
	private final ClassPathBeanDefinitionScanner scanner;


	/*
	 * @description
	 * 1.初始化bean的读取器和扫描器
	 * 2.如果直接调用这个默认构造方法，
	 * 需要在稍后调用其register()去注册配置类，并用refresh()刷新容器
	 * 触发容器对带有注解bean的载入、解析和注册过程
	 *
	 * @datetime 2020-3-2 0002 21:09
	 * Create a new AnnotationConfigApplicationContext that needs to be populated
	 * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
	 */
	public AnnotationConfigApplicationContext() {

		//会隐式调用父类的构造方法，初始化DefaultListableBeanFactory

		/*
		 * @description
		 * 注解bean定义读取器，主要作用是用来读取被注解的了bean
		 * @datetime 2020-3-2 0002 21:18
		 */
		this.reader = new AnnotatedBeanDefinitionReader(this);

		/*
		 * @description
		 * 1.扫描器，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
		 * 2.可以用来扫描包和类，继而转换成beanDefinition，但是实际上我们扫描包的工作并不是这个scanner对象完成的
		 * 是spring自己new的一个ClassPathBeanDefinitionScanner
		 * 3.这里的scanner仅仅是为了我们能够在外部调用AnnotationConfigApplicationContext的scan方法
		 * @datetime 2020-3-2 0002 21:20
		 */
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 */
	public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/*
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions
	 * from the given component classes and automatically refreshing the context.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 */
	public AnnotationConfigApplicationContext(Class<?>... componentClasses) {

		/*
		 * @author caijy
		 * @description
		 *	1.首先调用父类GenericApplicationContext的无参构造方法,得到一个名为DefaultListableBeanFactory的BeanFactory
		 *	2.本类的构造函数里面，初始化了一个读取器：AnnotatedBeanDefinitionReader read，
		 *    一个扫描器ClassPathBeanDefinitionScanner scanner
		 *    scanner的用处不是很大，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
		 * @datetime 2020-3-3 0003 22:18
		 */
		this();

		//关闭循环引用
//		setAllowCircularReferences(false);

		//把传入的类进行注册，这里有两个情况，
		//1.传入传统的配置类
		//2.传入bean（虽然一般没有人会这么做)
		//spring把传统的带上@Configuration的配置类称之为FULL配置类，不带@Configuration的称之为Lite配置类
		//但是我们这里先把带上@Configuration的配置类称之为传统配置类，不带的称之为普通bean
		//注册配置类
		register(componentClasses);

		//刷新容器
		refresh();
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, scanning for components
	 * in the given packages, registering bean definitions for those components,
	 * and automatically refreshing the context.
	 * @param basePackages the packages to scan for component classes
	 */
	public AnnotationConfigApplicationContext(String... basePackages) {
		this();
		scan(basePackages);
		refresh();
	}


	/**
	 * Propagate the given custom {@code Environment} to the underlying
	 * {@link AnnotatedBeanDefinitionReader} and {@link ClassPathBeanDefinitionScanner}.
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(environment);
		this.scanner.setEnvironment(environment);
	}

	/**
	 * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>Default is {@link org.springframework.context.annotation.AnnotationBeanNameGenerator}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.reader.setBeanNameGenerator(beanNameGenerator);
		this.scanner.setBeanNameGenerator(beanNameGenerator);
		getBeanFactory().registerSingleton(
				AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
	}

	/**
	 * Set the {@link ScopeMetadataResolver} to use for registered component classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.reader.setScopeMetadataResolver(scopeMetadataResolver);
		this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
	}


	//---------------------------------------------------------------------
	// Implementation of AnnotationConfigRegistry
	//---------------------------------------------------------------------

	/**
	 * Register one or more component classes to be processed.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #refresh()
	 */
	@Override
	public void register(Class<?>... componentClasses) {
		Assert.notEmpty(componentClasses, "At least one component class must be specified");
		this.reader.register(componentClasses);
	}

	/**
	 * Perform a scan within the specified base packages.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param basePackages the packages to scan for component classes
	 * @see #register(Class...)
	 * @see #refresh()
	 */
	@Override
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		this.scanner.scan(basePackages);
	}


	//---------------------------------------------------------------------
	// Convenient methods for registering individual beans
	//---------------------------------------------------------------------

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, and optionally providing explicit constructor
	 * arguments for consideration in the autowiring process.
	 * <p>The bean name will be generated according to annotated component rules.
	 * @param beanClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, Object... constructorArguments) {
		registerBean(null, beanClass, constructorArguments);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, and optionally providing explicit constructor
	 * arguments for consideration in the autowiring process.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Object... constructorArguments) {
		this.reader.doRegisterBean(beanClass, null, beanName, null,
				bd -> {
					for (Object arg : constructorArguments) {
						bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
					}
				});
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier,
			BeanDefinitionCustomizer... customizers) {

		this.reader.doRegisterBean(beanClass, supplier, beanName, null, customizers);
	}

}

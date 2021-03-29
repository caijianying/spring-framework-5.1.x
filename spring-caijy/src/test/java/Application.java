import com.caijy.UserService;
import com.caijy.X;
import com.caijy.Y;
import com.caijy.dao.UserMapper;
import com.caijy.mybatisspring.CaijInvocationHandler;
import com.caijy.mybatisspring.CaijyFactoryBean;
import com.caijy.mybatisspring.MySession;
import com.caijy.spring.AppConfig;
import com.caijy.spring.ClassMethodInterceptor;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class Application {

	/*
	 *测试spring装载bean的过程
	 */
	@Test
//	@Ignore
	public void test1(){
//		//实例化spring 容器对象
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
//		System.out.println(ac.getBean(Y.class));
//		UserService userService = ac.getBean(UserService.class);
//		System.out.println(userService.getUser());
//		System.out.println(ac.getBean(UserMapper.class));;
//		Class[] classes = new Class[]{UserMapper.class};
//		UserMapper userMapper = (UserMapper)Proxy.newProxyInstance(UserMapper.class.getClassLoader(), classes, new CaijInvocationHandler());
//		Map<String, Object> user = userMapper.getUser("7");
//		user.forEach((k,v)->System.out.println(k+" ==> "+v));

//		UserMapper userMapper = (UserMapper) MySession.getProxy(UserMapper.class);
//		Map<String, Object> user = userMapper.getUser("7");

		System.out.println(ac.getBean(UserMapper.class));
	}


	/*
	 *测试@configuration的cglib动态代理
	 */
//	@Test
	@Ignore
	public void test2(){
		/*
		 * 给X.class设置cglib动态代理
		 */
		Enhancer enhancer = new Enhancer();

		//想要cglib动态代理哪个类 就将该类设置为父类
		enhancer.setSuperclass(UserService.class);
		//创建一个方法拦截器 拦截目标类方法的执行 该拦截器实现MethodInterceptor接口 MethodInterceptor父接口为Callback
		enhancer.setCallback(new ClassMethodInterceptor());

		//产生代理对象 可以转型成为目标对象
		UserService uclass = (UserService)enhancer.create();
	}
}


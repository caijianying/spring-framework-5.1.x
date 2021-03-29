package com.caijy.spring;


//import com.caijy.annotation.CaijyMapperScan;
import com.caijy.annotation.CaijyMapperScan;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configurable
@ComponentScan("com.caijy")
@Configuration
@MapperScan("com.caijy.dao")
//@CaijyMapperScan("com.caijy.dao")
public class AppConfig {

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3721/test?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		factoryBean.setDataSource(dataSource);
		return factoryBean.getObject();
	}
}

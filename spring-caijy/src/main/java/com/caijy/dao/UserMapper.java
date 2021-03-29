package com.caijy.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;
public interface UserMapper {
	@Select("SELECT * FROM user WHERE id = #{id}")
	Map<String,Object> getUser(@Param("id") String userId);
}

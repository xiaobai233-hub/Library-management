package com.library;

import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
  @Select(
    "SELECT id,user_name AS username,password,CASE WHEN user_name='admin' THEN 'ADMIN' ELSE 'USER' END AS role,create_time AS createTime FROM library_user WHERE user_name=#{username}"
  )
  User findByUsername(String username);

  @Insert(
    "INSERT INTO library_user(user_name,password,create_time) VALUES(#{username},#{password},NOW())"
  )
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(User u);

  @Delete("DELETE FROM library_user WHERE id=#{id}")
  int delete(Long id);
}

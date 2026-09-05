package com.library.mapper;
import com.library.controller.*;
import com.library.service.*;
import com.library.mapper.*;
import com.library.pojo.*;
import com.library.exception.*;


import org.apache.ibatis.annotations.*;

@Mapper
/** 用户数据访问层，封装登录和账号操作。 */
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



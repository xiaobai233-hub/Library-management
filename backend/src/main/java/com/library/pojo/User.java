package com.library.pojo;
import com.library.controller.*;
import com.library.service.*;
import com.library.mapper.*;
import com.library.pojo.*;
import com.library.exception.*;


/** 用户实体，保存登录和角色信息。 */
public class User {

  public Long id;
  public String username;
  public String password;
  public String role;
  public java.time.LocalDateTime createTime;
}



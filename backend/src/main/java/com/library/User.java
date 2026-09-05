package com.library;

/** 用户实体，保存登录和角色信息。 */
public class User {

  public Long id;
  public String username;
  public String password;
  public String role;
  public java.time.LocalDateTime createTime;
}

package com.library;

import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
/** 提供登录、注册和账号注销接口。 */
public class AuthController {

  private final UserMapper mapper;
  private final BorrowRecordMapper borrows;
  private final ReaderMapper readers;

  public AuthController(
    UserMapper mapper,
    BorrowRecordMapper borrows,
    ReaderMapper readers
  ) {
    this.mapper = mapper;
    this.borrows = borrows;
    this.readers = readers;
  }

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody User input) {
    User u = mapper.findByUsername(input.username);
    if (
      u == null || !u.password.equals(input.password)
    ) throw new ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "用户名或密码错误"
    );
    Map<String, Object> result = new HashMap<>();
    result.put("id", u.id);
    result.put("username", u.username);
    result.put("role", u.role);
    return result;
  }

  @DeleteMapping("/users/{id}")
  public Map<String, String> deleteAccount(@PathVariable Long id) {
    if (borrows.activeReaderCount(id) > 0) throw new BorrowException(
      409,
      "账号还有未归还图书，不能注销，请先归还全部图书"
    );
    if (mapper.delete(id) == 0) throw new BorrowException(
      404,
      "账号不存在，无法注销"
    );
    return Collections.singletonMap("message", "账号已注销");
  }

  @PostMapping("/register")
  public Map<String, Object> register(@RequestBody Map<String, String> input) {
    String username = input.get("username"), password = input.get(
      "password"
    ), confirm = input.get("confirmPassword");
    if (
      username == null || username.trim().length() < 3
    ) throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "用户名至少 3 个字符"
    );
    if (
      password == null || password.length() < 6
    ) throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "密码至少 6 个字符"
    );
    if (!password.equals(confirm)) throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "两次输入的密码不一致"
    );
    if (
      mapper.findByUsername(username.trim()) != null
    ) throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
    User u = new User();
    u.username = username.trim();
    u.password = password;
    mapper.insert(u);
    Reader reader = new Reader();
    reader.id = u.id;
    reader.readerNo = "user-" + u.id;
    reader.readerName = u.username;
    readers.insert(reader);
    return Collections.singletonMap("message", "注册成功");
  }
}

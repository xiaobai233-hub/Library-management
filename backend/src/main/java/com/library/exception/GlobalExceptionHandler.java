package com.library.exception;
import com.library.controller.*;
import com.library.service.*;
import com.library.mapper.*;
import com.library.pojo.*;
import com.library.exception.*;


import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
/** 统一处理业务异常并转换为前端可读的错误响应。 */
public class GlobalExceptionHandler {

  @ExceptionHandler(BorrowException.class)
  public ResponseEntity<Map<String, Object>> handleBorrow(BorrowException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", e.getStatus());
    body.put("message", e.getMessage());
    return ResponseEntity.status(e.getStatus()).body(body);
  }
}



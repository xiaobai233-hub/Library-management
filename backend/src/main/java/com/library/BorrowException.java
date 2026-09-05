package com.library;

/** 借阅业务异常，用于返回库存或归还相关错误。 */
public class BorrowException extends RuntimeException {

  private final int status;

  public BorrowException(int status, String message) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}

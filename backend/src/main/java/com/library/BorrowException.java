package com.library;
public class BorrowException extends RuntimeException { private final int status; public BorrowException(int status,String message){super(message);this.status=status;} public int getStatus(){return status;} }

package com.library;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 图书实体，对应数据库中的 book 表。 */
public class Book {

  public Long id;
  public String bookName;
  public String author;
  public BigDecimal price;
  public Long categoryId;
  public Integer stock;
  public LocalDateTime createTime;
  public LocalDateTime updateTime;
  public Long version;
}

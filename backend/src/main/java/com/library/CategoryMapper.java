package com.library;

import java.util.*;
import org.apache.ibatis.annotations.*;

@Mapper
/** 图书分类数据访问层。 */
public interface CategoryMapper {
  @Select("SELECT * FROM book_category ORDER BY sort,id")
  List<Map<String, Object>> list();

  @Select("SELECT * FROM book_category WHERE id=#{id}")
  Map<String, Object> get(Long id);

  @Insert(
    "INSERT INTO book_category(category_name,sort) VALUES(#{categoryName},#{sort})"
  )
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Map<String, Object> c);

  @Update(
    "UPDATE book_category SET category_name=#{categoryName},sort=#{sort} WHERE id=#{id}"
  )
  int update(Map<String, Object> c);

  @Delete("DELETE FROM book_category WHERE id=#{id}")
  int delete(Long id);
}

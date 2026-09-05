package com.library.mapper;
import com.library.controller.*;
import com.library.service.*;
import com.library.mapper.*;
import com.library.pojo.*;
import com.library.exception.*;


import java.util.*;
import org.apache.ibatis.annotations.*;

@Mapper
/** 读者数据访问层。 */
public interface ReaderMapper {
  @Select(
    "SELECT id,reader_no AS readerNo,reader_name AS readerName FROM reader ORDER BY id DESC"
  )
  List<Reader> list();

  @Insert(
    "INSERT INTO reader(id,reader_no,reader_name) VALUES(#{id},#{readerNo},#{readerName})"
  )
  int insert(Reader r);
}



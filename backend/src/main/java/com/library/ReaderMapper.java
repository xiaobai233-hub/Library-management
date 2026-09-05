package com.library; import org.apache.ibatis.annotations.*; import java.util.*;
@Mapper public interface ReaderMapper { @Select("SELECT id,reader_no AS readerNo,reader_name AS readerName FROM reader ORDER BY id DESC") List<Reader> list(); @Insert("INSERT INTO reader(id,reader_no,reader_name) VALUES(#{id},#{readerNo},#{readerName})") int insert(Reader r); }

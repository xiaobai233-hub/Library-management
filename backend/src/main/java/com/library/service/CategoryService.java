package com.library.service;
import com.library.controller.*;
import com.library.service.*;
import com.library.mapper.*;
import com.library.pojo.*;
import com.library.exception.*;


import java.util.*;
import org.springframework.stereotype.Service;

@Service
/** 图书分类业务层。 */
public class CategoryService {

  private final CategoryMapper mapper;

  public CategoryService(CategoryMapper mapper) {
    this.mapper = mapper;
  }

  public List<Map<String, Object>> list() {
    return mapper.list();
  }

  public Map<String, Object> get(Long id) {
    return mapper.get(id);
  }

  public Map<String, Object> create(Map<String, Object> c) {
    mapper.insert(c);
    return c;
  }

  public Map<String, Object> update(Long id, Map<String, Object> c) {
    c.put("id", id);
    mapper.update(c);
    return mapper.get(id);
  }

  public void delete(Long id) {
    mapper.delete(id);
  }
}



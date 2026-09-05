package com.library.controller;
import com.library.controller.*;
import com.library.service.*;
import com.library.mapper.*;
import com.library.pojo.*;
import com.library.exception.*;


import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin
/** 提供读者信息查询和新增接口。 */
public class ReaderController {

  private final ReaderMapper mapper;

  public ReaderController(ReaderMapper mapper) {
    this.mapper = mapper;
  }

  @GetMapping
  public List<Reader> list() {
    return mapper.list();
  }

  @PostMapping
  public Reader create(@RequestBody Reader r) {
    mapper.insert(r);
    return r;
  }
}



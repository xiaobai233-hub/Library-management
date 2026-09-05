package com.library;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin
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

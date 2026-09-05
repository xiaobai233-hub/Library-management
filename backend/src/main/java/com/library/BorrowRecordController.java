package com.library;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow-records")
@CrossOrigin
public class BorrowRecordController {

  private final BorrowRecordService s;

  public BorrowRecordController(BorrowRecordService s) {
    this.s = s;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return s.list();
  }

  @GetMapping("/{id}")
  public Map<String, Object> get(@PathVariable Long id) {
    return s.get(id);
  }

  @PostMapping
  public Map<String, Object> create(@RequestBody Map<String, Object> r) {
    return s.create(r);
  }

  @PostMapping("/{id}/return")
  public Map<String, Object> returnBook(@PathVariable Long id) {
    return s.returnBook(id);
  }
}

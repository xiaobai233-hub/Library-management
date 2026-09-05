package com.library;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

  private final CategoryService s;

  public CategoryController(CategoryService s) {
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
  public Map<String, Object> create(@RequestBody Map<String, Object> c) {
    return s.create(c);
  }

  @PutMapping("/{id}")
  public Map<String, Object> update(
    @PathVariable Long id,
    @RequestBody Map<String, Object> c
  ) {
    return s.update(id, c);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    s.delete(id);
  }
}

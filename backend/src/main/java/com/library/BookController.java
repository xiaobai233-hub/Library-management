package com.library;

import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

  private final BookService service;

  public BookController(BookService service) {
    this.service = service;
  }

  @GetMapping
  public PageInfo<Book> list(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) Long categoryId
  ) {
    return service.list(page, size, keyword, categoryId);
  }

  @GetMapping("/{id}")
  public Book get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  public Book create(@RequestBody Book b) {
    return service.create(b);
  }

  @PutMapping("/{id}")
  public Book update(@PathVariable Long id, @RequestBody Book b) {
    return service.update(id, b);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}

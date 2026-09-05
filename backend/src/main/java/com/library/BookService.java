package com.library;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  private final BookMapper mapper;

  public BookService(BookMapper mapper) {
    this.mapper = mapper;
  }

  public PageInfo<Book> list(
    int page,
    int size,
    String keyword,
    Long categoryId
  ) {
    PageHelper.startPage(Math.max(page, 1), Math.max(size, 1));
    Book condition = new Book();
    condition.bookName = keyword;
    condition.author = keyword;
    condition.categoryId = categoryId;
    return new PageInfo<>(mapper.findAll(condition));
  }

  public Book get(Long id) {
    return mapper.findById(id);
  }

  public Book create(Book b) {
    mapper.insert(b);
    return b;
  }

  public Book update(Long id, Book b) {
    b.id = id;
    mapper.update(b);
    return mapper.findById(id);
  }

  public void delete(Long id) {
    mapper.delete(id);
  }
}

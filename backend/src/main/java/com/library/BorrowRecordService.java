package com.library;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BorrowRecordService {

  private final BorrowRecordMapper m;
  private final BookMapper books;

  public BorrowRecordService(BorrowRecordMapper m, BookMapper books) {
    this.m = m;
    this.books = books;
  }

  public List<Map<String, Object>> list() {
    return m.list();
  }

  public Map<String, Object> get(Long id) {
    return m.get(id);
  }

  @Transactional
  public Map<String, Object> create(Map<String, Object> r) {
    if (
      "ADMIN".equalsIgnoreCase(String.valueOf(r.get("role")))
    ) throw new BorrowException(403, "管理员账号不能借阅图书");
    if (!(r.get("readerId") instanceof Number)) throw new BorrowException(
      400,
      "借阅人信息无效"
    );
    if (!(r.get("bookId") instanceof Number)) throw new BorrowException(
      400,
      "图书信息无效"
    );
    Long readerId = ((Number) r.get("readerId")).longValue(), bookId =
      ((Number) r.get("bookId")).longValue();
    if (m.activeReaderCount(readerId) > 0) throw new BorrowException(
      409,
      "您已经借阅了一本书，请先归还后再借阅"
    );
    Book book = books.findById(bookId);
    if (book == null) throw new BorrowException(404, "图书不存在，无法借阅");
    if (book.stock <= 0) throw new BorrowException(
      409,
      "该图书库存不足，暂时无法借阅"
    );
    if (
      books.decreaseStock(bookId, book.version) == 0
    ) throw new BorrowException(409, "库存版本已变化，请刷新后重试");
    r.put("borrowTime", LocalDateTime.now());
    r.put("dueTime", LocalDateTime.now().plusDays(30));
    m.insert(r);
    return r;
  }

  @Transactional
  public Map<String, Object> returnBook(Long id) {
    Map<String, Object> r = m.get(id);
    if (r == null) throw new BorrowException(404, "借阅记录不存在");
    String status = String.valueOf(
      r.getOrDefault("STATUS", r.getOrDefault("status", ""))
    );
    if ("RETURNED".equalsIgnoreCase(status)) throw new BorrowException(
      409,
      "该图书已经归还，不能重复归还"
    );
    Object rawBookId = r.get("BOOK_ID");
    if (rawBookId == null) rawBookId = r.get("book_id");
    if (!(rawBookId instanceof Number)) throw new BorrowException(
      500,
      "借阅记录缺少图书信息"
    );
    Long bookId = ((Number) rawBookId).longValue();
    Book book = books.findById(bookId);
    if (book == null) throw new BorrowException(
      404,
      "关联图书不存在，无法归还"
    );
    if (m.returnBook(r) == 0) throw new BorrowException(
      409,
      "该借阅记录已被归还，请刷新后重试"
    );
    if (
      books.increaseStock(bookId, book.version) == 0
    ) throw new BorrowException(409, "库存版本已变化，请刷新后重试");
    return r;
  }
}

package com.jide.service;

import com.jide.domain.Book;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Transactional
public interface BookService {
    public boolean save(Book book);

    public boolean delete(Integer id);

    public boolean update(Book book);

    public Book getById(Integer id);

    public List<Book> getAll();
}

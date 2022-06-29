package com.jide.service.impl;

import com.jide.dao.BookDao;
import com.jide.domain.Book;
import com.jide.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookDao bookDao;

    public boolean save(Book book) {
        return bookDao.save(book) == 1;
    }

    public boolean delete(Integer id) {
        return bookDao.delete(id) == 1;
    }

    public boolean update(Book book) {
        return bookDao.update(book) == 1;
    }

    public Book getById(Integer id) {
        return bookDao.getById(id);
    }

    public List<Book> getAll() {
        return bookDao.getAll();
    }
}

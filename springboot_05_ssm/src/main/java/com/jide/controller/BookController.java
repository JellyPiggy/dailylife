package com.jide.controller;

import com.jide.domain.Book;
import com.jide.exception.BusinessException;
import com.jide.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public Result save(@RequestBody Book book) {
        boolean flag = bookService.save(book);
        return new Result(flag ? Code.SAVE_OK : Code.SAVE_ERR, flag);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        if (id < 0 ) {
            throw new BusinessException(Code.BUSINESS_ERR, "别瞎搞，你搞不到我！！");
        }
        boolean flag = bookService.delete(id);
        return new Result(flag ? Code.DELETE_OK : Code.DELETE_ERR, flag);
    }

    @PutMapping
    public Result update(@RequestBody Book book) {
        boolean flag = bookService.update(book);
        return new Result(flag ? Code.UPDATE_OK : Code.UPDATE_ERR, flag);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        if (id < 0 ) {
            throw new BusinessException(Code.BUSINESS_ERR, "别瞎搞，你搞不到我！！");
        }
        Book book = bookService.getById(id);
        Integer code = book == null ? Code.GET_ERR : Code.GET_OK;
        String message = book == null ? "数据查询失败，请稍后重试" : "";
        return new Result(code, book, message) ;
    }

    @GetMapping
    public Result getAll() {
        List<Book> all = bookService.getAll();
        Integer code = all == null ? Code.GET_ERR : Code.GET_OK;
        String message = all == null ? "数据查询失败，请稍后重试" : "";
        return new Result(code, all, message) ;
    }
}

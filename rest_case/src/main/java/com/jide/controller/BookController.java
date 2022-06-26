package com.jide.controller;

import com.jide.domain.Book;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@RestController
@RequestMapping("/books")
public class BookController {

    @PostMapping
    public String save(@RequestBody Book book) {
        System.out.println("book save " + book);
        return "{\"module\":\"haha\"}";
    }

    @GetMapping
    public List<Book> findAll() {
        System.out.println("book findAll");

        Book book1 = new Book();
        book1.setType("计算机");
        book1.setName("计算机网络");
        book1.setDescription("重要的基础知识");

        Book book2 = new Book();
        book2.setType("计算机");
        book2.setName("数据结构与算法");
        book2.setDescription("好书");

        List<Book> list = new ArrayList<Book>();
        list.add(book1);
        list.add(book2);

        return list;
    }

}

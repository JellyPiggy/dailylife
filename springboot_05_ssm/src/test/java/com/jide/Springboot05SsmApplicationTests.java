package com.jide;

import com.jide.domain.Book;
import com.jide.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Springboot05SsmApplicationTests {

    @Autowired
    private BookService bookService;

    @Test
    void test() {
        List<Book> all = bookService.getAll();
        System.out.println(all);
    }

}

package com.jide;

import com.jide.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 晓蝈
 * @version 1.0
 */
@SpringBootTest
public class AppTest {
    @Autowired
    private UserDao userDao;

    @Test
    public void test() {

    }
}

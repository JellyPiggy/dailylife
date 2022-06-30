package com.jide;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jide.dao.UserDao;
import com.jide.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class MybatisplusTest1ApplicationTests {

    @Autowired
    private UserDao userDao;

    @Test
    public void testSave() {
        User user = new User();
        user.setName("大猪");
        user.setPassword("13434");
        user.setAge(45);
        user.setTel("6451876153476");
        userDao.insert(user);
    }

    @Test
    void testGetAll() {
        List<User> users = userDao.selectList(null);
//        System.out.println(users);
        users.forEach(System.out::println);
    }

    @Test
    void testSelectPage(){
        //1 创建IPage分页对象,设置分页参数
        IPage<User> page=new Page<>(1,3);
        //2 执行分页查询
        userDao.selectPage(page,null);
        //3 获取分页结果
        System.out.println("当前页码值："+page.getCurrent());
        System.out.println("每页显示数："+page.getSize());
        System.out.println("总条数："+page.getTotal());
        System.out.println("总页数："+page.getPages());
        System.out.println("当前页数据："+page.getRecords());
    }

    @Test
    void testSelect1() {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lt("age", 10).or().gt("age", 30);
//        List<User> users = userDao.selectList(queryWrapper);
//        System.out.println(users);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.lt(User::getAge, 10).or().gt(User::getAge, 30);
        List<User> users = userDao.selectList(lambdaQueryWrapper);
        System.out.println(users);
    }

    @Test
    void testSelect2() {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lt("age", 18).gt("age", 10);
//        List<User> users = userDao.selectList(queryWrapper);
//        System.out.println(users);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
        lambdaQueryWrapper.lt(User::getAge, 30).gt(User::getAge, 10);
        List<User> users = userDao.selectList(lambdaQueryWrapper);
        System.out.println(users);
    }

    @Test
    void testSelect3() {
        Integer minAge = 10;
        Integer maxAge = null;
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
//        if (minAge != null) {
//            lambdaQueryWrapper.gt(User::getAge, minAge);
//        }
//        if (maxAge != null) {
//            lambdaQueryWrapper.lt(User::getAge, maxAge);
//        }
//        lambdaQueryWrapper.gt(minAge != null, User::getAge, minAge);
//        lambdaQueryWrapper.lt(maxAge != null, User::getAge, maxAge);
        lambdaQueryWrapper.gt(minAge != null, User::getAge, minAge)
                .lt(maxAge != null, User::getAge, maxAge);
        List<User> users = userDao.selectList(lambdaQueryWrapper);
        System.out.println(users);
    }

    @Test
    void testSelect4() {
//        QueryWrapper<User> lqw = new QueryWrapper<>();
//        lqw.select("name", "age", "tel");
//        List<User> userList = userDao.selectList(lqw);
//        System.out.println(userList);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getName, User::getAge, User::getTel);
        List<User> users = userDao.selectList(lambdaQueryWrapper);
        System.out.println(users);
    }

    @Test
    void testSelect5() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(*) num","max(age)", "tel");
        queryWrapper.groupBy("tel");
        List<Map<String, Object>> userList = userDao.selectMaps(queryWrapper);
        Map<String, Object> map = userList.get(0);
        System.out.println("============");
        for (String key : map.keySet()) {
            System.out.println("key=" + key + "  " + "value=" + map.get(key));
        }
        System.out.println(map);
    }

    @Test
    void testSelect6() {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.likeRight(User::getName, "j");
        List<User> users = userDao.selectList(lambdaQueryWrapper);
        System.out.println(users);
    }

    @Test
    void testSelect7() {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.between(User::getAge, 10, 30);
        List<User> users = userDao.selectList(lambdaQueryWrapper);
        System.out.println(users);
    }

    @Test
    void testSelect8() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("age");
        Page<User> page = new Page<>(1, 3);
        Page<User> userPage = userDao.selectPage(page, queryWrapper);
        System.out.println(userPage);
    }

    @Test
    void testLogicDelete() {
//        userDao.deleteById(1);
        //注意，逻辑删除也不会被mp的查询查到
        //如果想要查到得自己写sql语句
    }

    @Test
    void testUpdate() {
        /*User user = new User();
        user.setId(3L);
        user.setName("Jock666");
        user.setVersion(1);
        userDao.updateById(user);*/

        //1.先通过要修改的数据id将当前数据查询出来
        //User user = userDao.selectById(3L);
        //2.将要修改的属性逐一设置进去
        //user.setName("Jock888");
        //userDao.updateById(user);

        //1.先通过要修改的数据id将当前数据查询出来
        User user = userDao.selectById(3L);     //version=3
        User user2 = userDao.selectById(3L);    //version=3
        user2.setName("jerry aaa");
        userDao.updateById(user2);              //version=>4
        user.setName("jerry bbb");
        userDao.updateById(user);               //verion=3?条件还成立吗？
    }


    @Test
    void testTop5() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("age");
        Page<User> userPage = new Page<>(1, 5);
        userDao.selectPage(userPage, queryWrapper);
    }
}

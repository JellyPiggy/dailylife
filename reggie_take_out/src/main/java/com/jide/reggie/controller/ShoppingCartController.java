package com.jide.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jide.reggie.common.BaseContext;
import com.jide.reggie.common.R;
import com.jide.reggie.entity.ShoppingCart;
import com.jide.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author 晓蝈
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("用户添加新数据");
        //设置该次加的菜品或套餐的用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) { //如果这次加的数据是 dish
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else { //是setmeal
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询该次添加的菜品或套餐是否在该用户的购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (cartServiceOne != null) { //如果用户购物车中已有该菜品或套餐
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else { //用户如果没有该菜品或套餐
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success("添加成功");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("清空购物车");
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody Map<String, Long> map) {
        log.info("减少单个" + map.toString());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        Long dishId = map.get("dishId");
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, map.get("setmealId"));
        }
        ShoppingCart shoppingCart = shoppingCartService.getOne(queryWrapper);
        Integer number = shoppingCart.getNumber();
        if (number == 1) { //如果只有一个则直接删掉
            shoppingCartService.removeById(shoppingCart);
        } else { //如果有多个就在原来基础上减1
            shoppingCart.setNumber(number - 1);
            shoppingCartService.updateById(shoppingCart);
        }
        return R.success("删除成功");
    }
}

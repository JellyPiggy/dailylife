package com.jide.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jide.reggie.common.CustomException;
import com.jide.reggie.entity.Category;
import com.jide.reggie.entity.Dish;
import com.jide.reggie.entity.Setmeal;
import com.jide.reggie.mapper.CategoryMapper;
import com.jide.reggie.service.CategoryService;
import com.jide.reggie.service.DishService;
import com.jide.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，在删除前先看改分类是否关联了菜品或套餐
     * @param id
     * @return
     */
    public void deleteById(Long id) {
        //添加查询条件，根据分类id进行查询
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int dishCount = dishService.count(dishQueryWrapper);
        int setmealCount = setmealService.count(setmealQueryWrapper);
        if(dishCount > 0) {//如果已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        if(setmealCount > 0) {//如果已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //没有关联，正常删除分类
        super.removeById(id);
    }
}

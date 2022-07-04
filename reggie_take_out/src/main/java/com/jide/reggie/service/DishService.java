package com.jide.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jide.reggie.dto.DishDto;
import com.jide.reggie.entity.Dish;

/**
 * @author 晓蝈
 * @version 1.0
 */
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品及口味信息
    void updateWithFlavor(DishDto dishDto);
}

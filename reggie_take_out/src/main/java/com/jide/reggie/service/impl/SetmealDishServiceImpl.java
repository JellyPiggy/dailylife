package com.jide.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jide.reggie.entity.SetmealDish;
import com.jide.reggie.mapper.SetmealDishMapper;
import com.jide.reggie.service.SetmealDishService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}

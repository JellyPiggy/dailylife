package com.jide.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jide.reggie.dto.SetmealDto;
import com.jide.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}

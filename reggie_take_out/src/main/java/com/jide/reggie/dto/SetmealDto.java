package com.jide.reggie.dto;

import com.jide.reggie.entity.Setmeal;
import com.jide.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

package com.jide.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jide.reggie.entity.Category;

/**
 * @author 晓蝈
 * @version 1.0
 */
public interface CategoryService extends IService<Category> {
    void deleteById(Long id);
}

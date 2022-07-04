package com.jide.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jide.reggie.entity.ShoppingCart;
import com.jide.reggie.mapper.ShoppingCartMapper;
import com.jide.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}

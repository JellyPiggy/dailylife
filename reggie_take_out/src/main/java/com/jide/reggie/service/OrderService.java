package com.jide.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jide.reggie.entity.Orders;

/**
 * @author 晓蝈
 * @version 1.0
 */
public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}

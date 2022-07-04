package com.jide.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jide.reggie.entity.OrderDetail;
import com.jide.reggie.mapper.OrderDetailMapper;
import com.jide.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}

package com.jide.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jide.reggie.common.BaseContext;
import com.jide.reggie.common.CustomException;
import com.jide.reggie.entity.*;
import com.jide.reggie.mapper.OrderMapper;
import com.jide.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    //1.给order组装下面这些数据，payMethod 和 remark 及 addressBookId是传过来的
    // remark, payMethod,
    // id, number, status,  这里id不让它自己生成 设成和订单号number一样
    // orderTime, checkoutTime,
    // userId, phone, userName,    -------------User
    // addressBookId, address, consignee;  ----AddressBook
    // amount,                 ---------------ShoppingCart
    //2.遍历购物车，组装订单明细 并且累加得出amount
    //3.保存order，批量保存订单明细
    //4.清空购物车
    @Override
    @Transactional
    public void submit(Orders orders) {

        long orderId = IdWorker.getId();
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);

        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());

        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);
        User user = userService.getById(userId);
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());

        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(
                  (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );


        //查询用户购物车数据，设置收款金额及订单明细
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        //AtomicInteger 在高并发多线程情况下也不出错
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setImage(item.getImage());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setAmount(new BigDecimal(amount.get()));

        //向订单表和订单明细表中保存数据
        this.save(orders);
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}

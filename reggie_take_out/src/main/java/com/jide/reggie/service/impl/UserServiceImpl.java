package com.jide.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jide.reggie.entity.User;
import com.jide.reggie.mapper.UserMapper;
import com.jide.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

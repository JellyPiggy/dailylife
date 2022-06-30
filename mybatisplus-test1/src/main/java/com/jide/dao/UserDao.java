package com.jide.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jide.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
}

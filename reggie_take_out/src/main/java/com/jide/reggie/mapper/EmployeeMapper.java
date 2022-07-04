package com.jide.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jide.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

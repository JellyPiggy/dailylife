package com.jide.dao;

import com.jide.domain.Account;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
public interface AccountDao {
    @Select("select * from tbl_account")
    List<Account> selectAll();
}
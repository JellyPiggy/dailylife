package com.jide.service.imp;

import com.jide.dao.AccountDao;
import com.jide.domain.Account;
import com.jide.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Service
public class AccountServiceImp implements AccountService {

    @Autowired
    private AccountDao accountDao;

    public List<Account> findAll() {
        return accountDao.selectAll();
    }
}
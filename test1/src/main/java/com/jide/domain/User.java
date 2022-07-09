package com.jide.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Data
public class User {
    private Long id;
    private String name;
    private String password;
    private Integer age;
    private String tel;

    @TableLogic
    private Integer deleted;

    @Version
    private Integer version;
}

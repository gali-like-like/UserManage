package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.enums.UserRole;

import java.time.LocalDateTime;
@Data
@TableName("users")
public class User {

    @TableId(type= IdType.AUTO)
    private Integer id;//账号id

    private String username;//账号

    private String password;//密码

    private String phone;//手机号

    private Integer userRole;//用户角色

    private String name;//昵称

    private String image;//头像

    private String description;//个人简介

    private Boolean isDelete;//是否删除

    private LocalDateTime deleteTime;//删除日期

    private LocalDateTime createTime;//创建日期时间

    private LocalDateTime updateTime;//更新的日期时间
}

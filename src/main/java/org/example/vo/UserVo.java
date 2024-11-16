package org.example.vo;

import lombok.Data;
import org.example.enums.UserRole;

@Data
public class UserVo {

    private String username;

    private String name;

    private String image;

    private String phone;

    private String role;

    private String description;

}

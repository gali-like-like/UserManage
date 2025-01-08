package org.example.vo;

import lombok.Data;
import org.example.enums.UserRole;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String name;

    private String image;

    private String phone;

    private String role;

    private String description;

    private String grender;

    private LocalDate birthDay;
}

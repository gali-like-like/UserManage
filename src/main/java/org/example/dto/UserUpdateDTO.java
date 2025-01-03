package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {

    private String name;

    private String phone;

    private Boolean grender;//性别,true为男,false为女

    private LocalDate birthDay;//生日

    private String password;

    private String description;
}

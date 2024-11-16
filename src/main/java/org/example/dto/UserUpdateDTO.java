package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDTO {

    private String name;

    private String phone;

    private String password;

    private String description;
}

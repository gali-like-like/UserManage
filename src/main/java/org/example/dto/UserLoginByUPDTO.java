package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UserLoginByUPDTO {

    @NotNull(message = "账号不能为空")
    private String username;//账号

    @NotNull(message = "密码不能为空")
    private String password;//密码
}

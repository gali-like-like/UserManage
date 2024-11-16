package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Valid
public class UserLoginByPhoneDTO {

    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误")
    private String phone;
    @Max(value = 999999,message = "最大不能超过999999")
    @Min(value = 100000,message = "最小不能少于100000")
    private Integer code;

}

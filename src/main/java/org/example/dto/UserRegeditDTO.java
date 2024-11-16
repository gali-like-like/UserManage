package org.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.enums.UserRole;

@Data
public class UserRegeditDTO {

    private String name;//昵称
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误")
    private String phone;//手机号

    @Max(value = 999999,message = "最大不能超过999999")
    @Min(value = 100000,message = "最小不能少于100000")
    private Integer code;
}

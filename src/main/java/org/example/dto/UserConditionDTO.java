package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.enums.UserRole;

import java.time.LocalDate;

@Data
public class UserConditionDTO {

    private String name;

    private Integer userRole;
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误")
    private String phone;

    private String username;

    private Boolean grender;//性别,true为男性,false为女性

    @PastOrPresent(message = "开始日期不能是未来的日期")
    private LocalDate begin;

    @PastOrPresent(message = "结束日期不能是未来的日期")
    private LocalDate end;

    @NotNull(message = "页码不能为空")
    @Min(value=1L,message = "页码最小为1")
    private Integer page;

    @NotNull(message = "页面大小不能为空")
    @Min(value=1L,message = "页面大小最小为1")
    private Integer pageSize;
}

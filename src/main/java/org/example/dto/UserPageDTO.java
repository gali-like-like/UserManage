package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPageDTO {
    @NotNull(message = "页码不能为空")
    @Min(value=1L,message = "页码最小为1")
    private Integer page;
    @NotNull(message = "分页数量不能为空")
    @Min(message = "分页数量必须大于等于5", value = 5L)
    private Integer pageSize;
}

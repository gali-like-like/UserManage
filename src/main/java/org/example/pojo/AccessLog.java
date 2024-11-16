package org.example.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessLog {
    private String ip;//客户端公网ip
    private String method;//http请求方式
    private String uri;//访问资源
    private String handlerMethod;//处理函数
    private Integer code;//http状态码
    private Long begin;//请求开始时间
    private Long end;//请求结束时间
    private Long cost;//请求耗时

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%d,%d",ip,method,uri,handlerMethod,code,cost);
    }

}

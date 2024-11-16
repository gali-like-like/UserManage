package org.example.enums;

public enum JwtParseStatus {
    JWTPARSEERROR("Jwt解析失败"),JWTTIMEOUT("Jwt已过期"),JWTSUCCESS("Jwt解析成功");
    private String desc;
    private JwtParseStatus(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return this.desc;
    }
}

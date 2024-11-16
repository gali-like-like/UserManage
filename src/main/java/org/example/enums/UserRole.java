package org.example.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public enum UserRole implements IEnum<String> {
    NORMAL("普通用户"),
    SUPER("超级用户");
    private String desc;
    private UserRole(String desc) {
        this.desc = desc;
    }

    @JsonCreator
    public UserRole fromString(String desc) {
        return Enum.valueOf(UserRole.class, desc);
    }

    @JsonValue
    public String getValue() {
        return this.desc;
    }

    public static class UserRoleDeserializer extends JsonDeserializer<UserRole> {
        @Override
        public UserRole deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String value = p.getValueAsString();
            if (value == null || value.isEmpty()) {
                // 可以选择返回默认值，抛出异常，或处理为null（如果枚举支持null）
                throw new IOException("Cannot deserialize empty string to UserRole");
            }
            return UserRole.valueOf(value.toUpperCase());
        }
    }
}

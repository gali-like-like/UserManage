package org.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annos.RoleLimit;
import org.example.context.BaseContext;
import org.example.entity.Result;
import org.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class RoleLimitAspect {

    @Autowired
    private UserMapper userMapper;

    @Around("@annotation(roleLimit)")
    public Object checkRoleLevel(ProceedingJoinPoint joinPoint, RoleLimit roleLimit) throws Throwable {
        // 假设你有一个方法来获取当前用户的角色级别
        String username = BaseContext.getCurrentId();
        log.info("username = {}", username);
        // 获取当前用户username
        Integer currentUserRole = userMapper.getUserRoleByUsername(username);
        log.info("currentUserRole = {}", currentUserRole);
        // 允许权限列表里是否有当前用户对应的角色等级，如果有就继续执行
        int[] needRoles = roleLimit.roleLevel();
        boolean isExests = Arrays.stream(needRoles).anyMatch(item -> item == currentUserRole);
        if (isExests) {
            return joinPoint.proceed();
        } else {
            return Result.error("权限不够");
        }
    }

}

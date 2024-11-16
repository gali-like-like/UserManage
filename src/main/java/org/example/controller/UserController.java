package org.example.controller;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.example.annos.RoleLimit;
import org.example.context.BaseContext;
import org.example.dto.*;
import org.example.entity.Result;
import org.example.entity.User;
import org.example.message.CommonMessage;
import org.example.service.UserService;
import org.example.tool.Base64Tool;
import org.example.tool.SMSTool;
import org.example.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/main")
@Tag(name="员工相关操作")
@Slf4j
public class UserController {
    /**
     * 员工的条件查询
     * 员工的分页查询
     *
     * **/

    @Autowired
    private UserService userService;

    @Autowired
    private Base64Tool base64Tool;

    @Autowired
    private SMSTool smsTool;

    @GetMapping("/user/username/{username}")
    @Cacheable(cacheNames = "user",key="#username",unless = "#username == null")
    @Operation(summary = "根据账号查询员工")
    @RoleLimit(roleLevel = {1,2})
    public Result findUserByUsername(@Validated @NotNull(message = "用户账号不能为空") @PathVariable String username) {
        UserVo user = userService.getUserVoByUsername(username);
        return Result.success(user);
    }

    @Operation(summary = "批量删除用户")
    @PutMapping("/users")
    @RoleLimit(roleLevel = {2})
    public Result deleteBatchUsers(@RequestParam(name = "usernames") List<String> usernames) {
        log.info("usernames: {}", usernames);
        String info = userService.deleteBatchUsers(usernames);
        if (info.equals(CommonMessage.DELETE_USER_SUCCESS))
            return Result.success(info,null);
        else
            return Result.error(info);
    }

    @Operation(summary = "批量重置密码")
    @PutMapping("/users/reset")
    @RoleLimit(roleLevel = {2})
    public Result resetBatchUsers(@RequestParam(name="usernames") List<String> usernames) {
        log.info("usernames: {}", usernames);
        String info =  userService.resetBatchUsers(usernames);
        if (info.equals(CommonMessage.RESET_PASSWORD_SUCCESS))
            return Result.success(info,null);
        else
            return Result.error(info);
    }

    @Operation(summary = "根据条件查询员工")
    @PostMapping("/users/condition")
    @RoleLimit(roleLevel = {1,2})
    public Result findUsersByCondition(@Validated @RequestBody UserConditionDTO userConditionDTO) throws ExecutionException, InterruptedException, TimeoutException {
        log.info("欢迎光临");
        Page<UserVo> userVoPage = userService.getUsersByCondition(userConditionDTO);
        log.info("users:{}",userVoPage);
        return Result.success(userVoPage);
    }

    @PostMapping("/add")
    public Result add(@Validated @RequestBody User user) throws DataAccessException, NoSuchAlgorithmException {
        Integer index = userService.add(user);
        return Result.success(CommonMessage.REGEDIT_SUCCESS,index);
    }

    @PostMapping("/user/regedit")
    @Operation(summary = "注册")
    public Result regedit(@Validated @RequestBody UserRegeditDTO userRegeditDTO) throws NoSuchAlgorithmException {
        String username = userService.regedit(userRegeditDTO);
        if (username.equals(CommonMessage.CODE_ERROR)) {
            return Result.error(username);
        }
        return Result.success(CommonMessage.REGEDIT_SUCCESS,username);
    }

    @Operation(summary = "根据账号密码登录")
    @PostMapping("/login/up")
    public Result login(@Validated @RequestBody UserLoginByUPDTO loginByUPDTO) throws NoSuchAlgorithmException {
        String jwtOrDesc = userService.loginByUP(loginByUPDTO);
        log.info("结果:{}",jwtOrDesc);
        //todo 生成jwt
        if (jwtOrDesc.equals(CommonMessage.USER_NULL) || jwtOrDesc.equals(CommonMessage.NOMATCH))
            return Result.error(jwtOrDesc);
        return Result.success(CommonMessage.LOGIN_SUCCESS,jwtOrDesc);
    }

    @Operation(summary = "根据手机号登录")
    @PostMapping("/login/phone")
    public Result login(@Validated @RequestBody UserLoginByPhoneDTO loginByPhone) throws NoSuchAlgorithmException {
        String jwtOrDesc = userService.loginByPhone(loginByPhone);
        log.info("结果:{}",jwtOrDesc);
        if (jwtOrDesc.equals(CommonMessage.NOMATCH) || jwtOrDesc.equals(CommonMessage.CODE_ERROR)) {
            //todo 生成jwt
            return Result.error(jwtOrDesc);
        }
        else
            return Result.success(CommonMessage.LOGIN_SUCCESS,jwtOrDesc);
    }

    @PutMapping("/logout")
    @Operation(summary = "注销员工")
    @CacheEvict(cacheNames = "user",key="#username")
    @RoleLimit(roleLevel = {1})
    public Result logout(String username) {
        String info = userService.logout(username);
        if (info.equals(CommonMessage.DELETE_USER_SUCCESS))
            return Result.success(info,null);
        else
            return Result.error(info);
    }

    @PutMapping("/user")
    @Operation(summary = "修改员工信息")
    @RoleLimit
    public Result update(@Validated @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.save(userUpdateDTO);
        return Result.success("更新超过");
    }

    @PostMapping("/code")
    @Operation(summary = "获取验证码")
    @Cacheable(cacheNames = "phoneCode",key = "#phone",condition = "#phone != null")
    public Result getCode(@Valid @NotNull(message = "手机号不能为空") String phone) throws Exception {
        Integer code = smsTool.send(phone);
        return Result.success(code);
    }

    @PutMapping("/user/update_header")
    @Operation(summary = "更新头像")
    @RoleLimit(roleLevel = {1,2})
    public Result updateHeader(MultipartFile file) throws IOException, ClientException {
        String imageOrMsg = userService.updateHeader(file, BaseContext.getCurrentId());
        if (imageOrMsg != CommonMessage.USER_NULL) {
            return Result.success(imageOrMsg);
        }
        else
            return Result.error(imageOrMsg);
    }

    @PutMapping("/reset_password")
    @Operation(summary = "重置密码")
    @RoleLimit(roleLevel = {2})
    public Result resetPassword(String username) {
        String info =  userService.resetPasswordByPassword(username);
        return Result.success(info,null);
    }

    @PostMapping("/get_all")
    @Operation(summary = "获取用户数据")
    public Result getUsers() {
        List<UserVo> userVos = userService.getUsers();
        return Result.success(userVos);
    }

}

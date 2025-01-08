package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.dto.UserConditionDTO;
import org.example.dto.UserUpdateDTO;
import org.example.entity.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Integer totalByCondition(UserConditionDTO userConditionDTO);

    List<User> pageByCondition(UserConditionDTO userConditionDTO);

    void save(UserUpdateDTO userUpdateDTO);

    Integer getUserRoleByUsername(String username);

    void resetPasswordByUsername(String username);
    //批量删除用户
    void deleteBatchUsers(List<String> usernames);
    //批量重置密码
    void resetBatchUsers(List<String> usernames);

    String getUserHeaderByUsername(String username);
}

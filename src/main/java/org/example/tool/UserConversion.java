package org.example.tool;

import org.example.entity.User;
import org.example.vo.UserVo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class UserConversion {
    //转换类
    public UserVo userVoFromUser(@NotNull User user) {
        UserVo userVo = new UserVo();
        userVo.setRole(user.getUserRole()==1?"普通用户":"管理员");
        userVo.setUsername(user.getUsername());
        userVo.setImage(user.getImage());
        String phone = user.getPhone();
        String replacePhone = phone;
//        String replacePhone = phone.replace(phone.substring(3,7),"*****");
        userVo.setPhone(replacePhone);
        userVo.setName(user.getName());
        userVo.setDescription(user.getDescription());
        return userVo;
    }

}

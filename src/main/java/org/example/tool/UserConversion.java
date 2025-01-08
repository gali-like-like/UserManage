package org.example.tool;

import com.aliyuncs.exceptions.ClientException;
import org.example.entity.User;
import org.example.vo.UserVo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserConversion {
    //转换类

    public UserVo userVoFromUser(@NotNull User user) throws ClientException {
        UserVo userVo = new UserVo();
        userVo.setRole(user.getUserRole()==1?"普通用户":"管理员");
        userVo.setUsername(user.getUsername());
        //将数据库的oss路径转成图像的url
        if (Objects.nonNull(user.getImage())) {
            String ossUrl = user.getImage();
            String imageUrl = OSSTool.getAccessUrl(ossUrl);
            userVo.setImage(imageUrl);
        } else {
            userVo.setImage(null);
        }
        String phone = user.getPhone();
        //隐藏处理
        String replacePhone = phone.replace(phone.substring(3,7),"*****");
        userVo.setPhone(replacePhone);
        userVo.setName(user.getName());
        userVo.setDescription(user.getDescription());
        userVo.setGrender(user.getGrender()?"男":"女");
        userVo.setBirthDay(user.getBirthDay());
        return userVo;
    }

}

package org.example.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.tools.jconsole.JConsoleContext;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.entity.Result;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.message.CommonMessage;
import org.example.tool.JwtTool;
import org.example.tool.OSSTool;
import org.example.tool.UserConversion;
import org.example.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserConversion userConversion;

    @Autowired
    private RedisTemplate redisTemplate;

    public Page<UserVo> getUsers(UserPageDTO userPageDTO) throws DataAccessException {
        Page<User> pageUsers = new Page<>(userPageDTO.getPage(), userPageDTO.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<UserVo> userVos =  userMapper.selectPage(pageUsers,queryWrapper).getRecords().parallelStream().map(item -> userConversion.userVoFromUser(item)).collect(Collectors.toList());
        Page<UserVo> userVoPage = new Page<>(pageUsers.getCurrent(),pageUsers.getSize(),pageUsers.getTotal());
        userVoPage.setRecords(userVos);
        return userVoPage;
    }

    public String resetPasswordByPassword(String username) {
        if (StringUtil.isNullOrEmpty(username)) {
            return CommonMessage.USER_NULL;
        } else {
            int roleLevel = userMapper.getUserRoleByUsername(username);
            if(roleLevel == 2) {
                return CommonMessage.PRIVIAGE_REJECT;
            }
        }
        userMapper.resetPasswordByUsername(username);
        return CommonMessage.RESET_PASSWORD_SUCCESS;
    }

    public User getUserById(Integer id) throws DataAccessException {
        return userMapper.selectById(id);
    };

    public UserVo getUserVoByUsername(String username) throws DataAccessException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", false)
                .and(wrapper -> wrapper.gt("username", username));
        User user = userMapper.selectOne(queryWrapper);
        return userConversion.userVoFromUser(user);
    }
    //加油
    public List<UserVo> getUsers() {
        List<User> users = userMapper.selectList(null);
        log.info("users:{}",users);
        List<UserVo> userVos = new ArrayList<>();
        ((ArrayList<UserVo>) userVos).ensureCapacity(users.size());
        log.info("len:{}",users.size());
        for (User user:users) {
            UserVo userVo = userConversion.userVoFromUser(user);
            userVos.add(userVo);
            log.info(userVo.toString());
        }
        return userVos;
//        return users.stream().map(item -> userConversion.userVoFromUser(item)).collect(Collectors.toList());
    }

    public String deleteBatchUsers(List<String> usernames) throws DataAccessException {
        log.info("usernames:{}",usernames);
        boolean isExsts = usernames.stream().anyMatch(username -> username.equals("\"galiLikeLike1730219047\""));
        if (isExsts) {
            return CommonMessage.PRIVIAGE_REJECT;
        }
        if (Objects.isNull(usernames)) {
            return CommonMessage.PARAM_NOT_NULL;
        } else if(usernames.size() == 0) {
            return CommonMessage.PARAM_NOT_NULL;
        }
        else {
            userMapper.deleteBatchUsers(usernames);
            return CommonMessage.DELETE_USER_SUCCESS;
        }
    }

    public String resetBatchUsers(List<String> usernames) throws DataAccessException {
        boolean isExsts = usernames.stream().anyMatch(username -> username.equals("\"galiLikeLike1730219047\""));
        if (isExsts) {
            return CommonMessage.PRIVIAGE_REJECT;
        }
        if (Objects.isNull(usernames)) {
            return CommonMessage.PARAM_NOT_NULL;
        } else if(usernames.size() == 0) {
            return CommonMessage.PARAM_NOT_NULL;
        }

        userMapper.resetBatchUsers(usernames);
        return CommonMessage.RESET_PASSWORD_SUCCESS;
    }

    public User getUserByUserName(String username) throws DataAccessException {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username",username);
        return userMapper.selectOne(userQueryWrapper);
    }

    public User getUserByPhone(String phone) throws DataAccessException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
        return userMapper.selectOne(queryWrapper);
    }

    public Page<UserVo> getUsersByCondition(UserConditionDTO userConditionDTO) throws DataAccessException, ExecutionException, InterruptedException, TimeoutException {
        Page<UserVo> page = new Page<>(userConditionDTO.getPage(), userConditionDTO.getPageSize());
        userConditionDTO.setPage((userConditionDTO.getPage()-1)*userConditionDTO.getPageSize());
        CompletableFuture<List<UserVo>> pageFuture = CompletableFuture.supplyAsync(() -> userMapper.pageByCondition(userConditionDTO))
                .handle((res,e) -> {
                    if (Objects.isNull(e)) {
                        return res.stream().map(user -> userConversion.userVoFromUser(user)).collect(Collectors.toList());
                    } else {
                        log.error(e.getLocalizedMessage());
                        return null;
                    }
                });
        CompletableFuture<Integer> totalFuture = CompletableFuture.supplyAsync(() -> {
            return userMapper.totalByCondition(userConditionDTO);
        }).handle((res, e) -> {
            if(Objects.isNull(e)) {
                return res;
            } else {
                log.error(e.getLocalizedMessage());
                return null;
            }
        });
        CompletableFuture<Void> resultFuture = CompletableFuture.allOf(totalFuture,pageFuture);
        resultFuture.get(3, TimeUnit.SECONDS);
        page.setRecords(pageFuture.get());
        page.setTotal(totalFuture.get());
        return page;
    }

    public void save(UserUpdateDTO userUpdateDTO) throws DataAccessException {
        log.info("更新中");

        userMapper.save(userUpdateDTO);
    }



    public void remove(Integer id) throws DataAccessException {
        userMapper.deleteById(id);
    }

    public String logout(String username) throws DataAccessException {
        if (username.equals("galiLikeLike1730219047")) {
            return CommonMessage.PRIVIAGE_REJECT;
        }
        User user = this.getUserByUserName(username);
        user.setIsDelete(true);
        user.setDeleteTime(LocalDateTime.now());
        userMapper.updateById(user);
        return CommonMessage.DELETE_USER_SUCCESS;
    }

    public Integer add(User user) throws DataAccessException, NoSuchAlgorithmException {
        String password = user.getPassword();
        String sha256Pd = this.toSha256(password);
        user.setPassword(sha256Pd);
        userMapper.insert(user);
        return 1;
    }

    public String toSha256(String initStr) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] data = digest.digest(initStr.getBytes(StandardCharsets.UTF_8));//前端传过来的密码加密
        StringBuffer hexString = new StringBuffer();
        for (int i = 0;i<data.length;i++) {
            String hex = Integer.toHexString(0xff&data[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    public String regedit(UserRegeditDTO regeditDTO) throws DataAccessException, NoSuchAlgorithmException {
        String phone = regeditDTO.getPhone();
        String codeResult = this.checkCode(phone,regeditDTO.getCode());
        if (codeResult.equals(CommonMessage.CODE_RIGHT)) {
            return codeResult;
        }
        User user = new User();
        user.setName(regeditDTO.getName());
        user.setPhone(regeditDTO.getPhone());
        user.setPassword("123456");
        user.setUserRole(1);
        long timestramp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        log.info("timestramp:{}",timestramp);
        String username = user.getName()+String.valueOf(timestramp);
        user.setUsername(username);
        this.add(user);
        return username;
    }

    public String loginByUP(UserLoginByUPDTO userLoginByUPDTO) throws NoSuchAlgorithmException {
        String username = userLoginByUPDTO.getUsername();
        User user = this.getUserByUserName(username);
        if (Objects.isNull(user)) {
            return CommonMessage.USER_NULL;
        }
        String password = user.getPassword();//注册时密码都是经过sha256加密的，所以数据库里的密码都加密了
        log.info("db password:{}",password);
        String inputPassword = userLoginByUPDTO.getPassword();
        String hexString = this.toSha256(inputPassword);
        log.info("before password:{}",hexString);

        if (hexString.toString().equals(password)) {
            //生成jwt
            HashMap<String,Object> items = new HashMap<>();
            items.put("username",username);
            items.put("name",user.getName());
            items.put("image",user.getImage());
            items.put("description",user.getDescription());
            items.put("userRole",user.getUserRole());
            items.put("phone",user.getPhone());
            String jwt = JwtTool.getJwt(items);
            return jwt;
        }else
            return CommonMessage.NOMATCH;

    }

    private String checkCode(String phone,Integer inputCode) {
        Result result = (Result) redisTemplate.opsForValue().get("phoneCode::"+phone);
        Integer code = (Integer) result.getData();
        log.info("code:{}",code);
        if (Objects.isNull(code)) {
            return CommonMessage.CODE_ERROR;
        }
        else if(!code.equals(inputCode)) {
            return CommonMessage.CODE_ERROR;
        } else {
            return CommonMessage.CODE_RIGHT;
        }
    }

    public String loginByPhone(UserLoginByPhoneDTO userLoginByPhoneDTO) throws DataAccessException {
        String phone = userLoginByPhoneDTO.getPhone();
        String codeResult = this.checkCode(phone,userLoginByPhoneDTO.getCode());
        if (!codeResult.equals(CommonMessage.CODE_RIGHT)) {
            return codeResult;
        }
        User user = this.getUserByPhone(phone);
        if (Objects.isNull(user))
            return CommonMessage.USER_NULL;
        HashMap<String,Object> items = new HashMap<>();
        items.put("username",user.getUsername());
        items.put("name",user.getName());
        items.put("image",user.getImage());
        items.put("description",user.getDescription());
        items.put("userRole",user.getUserRole());
        items.put("phone",user.getPhone());
        String jwt = JwtTool.getJwt(items);
        return jwt;
    }

    public String updateHeader(MultipartFile file,String username) throws IOException, ClientException {
        User user = this.getUserByUserName(username);
        if (Objects.isNull(user)) {
            return CommonMessage.USER_NULL;
        }
        String image = user.getImage();
        if (StringUtil.isNullOrEmpty(image)) {
            //从来没使用过头像,更新头像就是上传头像
            String uuid = UUID.randomUUID().toString();
            String objectPath = String.format("headers/%s_%s",uuid,file.getOriginalFilename());
            OSSTool.uploadFile(objectPath,file.getInputStream());
            String fileUrl = OSSTool.getAccessUrl(objectPath);
            return fileUrl;
        }
        else {
            OSSTool.uploadFile(image,file.getInputStream());
            return image;
        }
    }

    //自动删除过期用户
    @Scheduled(cron = "0 12 0 * * ?")
    public void autoremove() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",true)
                .and(wrap -> wrap.le("delete_time",LocalDateTime.now().minusMonths(1)));
        List<User> users = userMapper.selectList(queryWrapper);
        userMapper.delete(queryWrapper);
    }
}

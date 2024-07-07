package org.qiyu.live.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.interfaces.rpc.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author :jianggq
 * @Date :2024/6/27
 * Description :
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @DubboReference
    private IUserRpc userRpc;

    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId) {
        return userRpc.getByUserId(userId);
    }


    @GetMapping("/updateUserInfo")
    public boolean updateUserInfo(Long userId,String nickname) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName(nickname);
        return userRpc.updateUserInfo(userDTO);
    }


    @GetMapping("/insertUserInfo")
    public boolean insertUserInfo(Long userId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName("测试");
        userDTO.setSex(1);
        return userRpc.insertOne(userDTO);
    }

    @GetMapping("/batchQueryUserInfo")
    public Map<Long, UserDTO> batchQueryUserInfo(String userIdStr){
        return userRpc.batchQueryUserInfo(Arrays.asList(userIdStr.split(",")).stream().map(x->Long.valueOf(x)).collect(Collectors.toList()));
    }
}

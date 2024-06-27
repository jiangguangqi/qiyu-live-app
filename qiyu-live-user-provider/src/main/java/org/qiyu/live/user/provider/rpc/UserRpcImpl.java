package org.qiyu.live.user.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.user.interfaces.rpc.IUserRpc;

/**
 * @Author :jianggq
 * @Date :2024/6/25
 * Description :
 */
@DubboService
public class UserRpcImpl implements IUserRpc {


//    @Resource
//    private IUserService userService;

    @Override
    public String test() {
        System.out.println("this is dobbu test");
        return "success";
    }

//    @Override
//    public UserDTO getbyUserId(Long userId) {
//        return userService.getByUserId(userId);
//    }
}

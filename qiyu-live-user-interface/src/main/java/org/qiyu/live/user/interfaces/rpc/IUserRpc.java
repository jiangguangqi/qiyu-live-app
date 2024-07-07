package org.qiyu.live.user.interfaces.rpc;

import org.qiyu.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author :jianggq
 * @Date :2024/6/25
 * Description :
 */
public interface IUserRpc {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    UserDTO getByUserId(Long userId);

    /**
     * 更新用户信息
     * @param userDTO
     * @return
     */
    boolean updateUserInfo(UserDTO userDTO);

    /**
     * 插入一条用户信息
     * @param userDTO
     * @return
     */
    boolean insertOne(UserDTO userDTO);

    /**
     * 批量查询用户信息
     * @param userIdList
     * @return
     */
    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList);
}

//package org.qiyu.live.user.provider.service.impl;
//
//import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
//import org.qiyu.live.user.dto.UserDTO;
//import org.qiyu.live.user.provider.dao.mapper.IUserMapper;
//import org.qiyu.live.user.provider.service.IUserService;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//
///**
// * @Author :jianggq
// * @Date :2024/6/24
// * Description :
// */
//@Service
//public class UserServiceImpl implements IUserService {
//
//    @Resource
//    private IUserMapper userMapper;
//
//
//    @Override
//    public UserDTO getByUserId(Long userId) {
//        if(userId == null){
//            return null;
//        }
//        return ConvertBeanUtils.convert(userMapper.selectByUserId(userId), UserDTO.class);
//    }
//}

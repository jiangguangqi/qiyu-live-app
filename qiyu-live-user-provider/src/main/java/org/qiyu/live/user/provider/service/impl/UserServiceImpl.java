package org.qiyu.live.user.provider.service.impl;


import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserMapper;
import org.qiyu.live.user.provider.dao.po.UserPO;
import org.qiyu.live.user.provider.service.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Author :jianggq
 * @Date :2024/6/24
 * Description :
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public UserDTO getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        //String key = "userInfo" + userId;
        UserDTO userDTO = (UserDTO) redisTemplate.opsForValue().get(key);
        if (userDTO != null) {
            return userDTO;
        }
        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if (userDTO != null) {
            redisTemplate.opsForValue().set(key, userDTO);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }
        userIdList = userIdList.stream().filter(id -> id > 1000).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }
        //性能不太好
        //底层使用的union all
        //userMapper.selectBatchIds(userIdList);

        //redis
        /*效果很差
        userIdList.forEach(userId -> {
                    redisTemplate.opsForValue().get("");
        });*/
        // 创建一个用于存储缓存键的列表
        List<String> keyList = new ArrayList<>();

        // 遍历 userIdList，生成每个用户的缓存键，并添加到 keyList 中
        userIdList.forEach(userId -> {
            keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });

        // 从 Redis 缓存中批量获取所有缓存键对应的用户信息
        List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(keyList).stream()
                .filter(x -> x != null)  // 过滤掉为 null 的结果
                .map(object -> (UserDTO) object)  // 将 Object 转换为 UserDTO
                .collect(Collectors.toList());  // 收集到 userDTOList 列表中

        // 如果缓存中所有用户信息都存在，并且数量与 userIdList 相同，则直接返回结果
        if (!CollectionUtils.isEmpty(userDTOList) && userDTOList.size() == userIdList.size()) {
            return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));  // 转换为 Map 并返回
        }

        // 提取缓存中存在的用户 ID，生成一个列表 udesIdInCacheList
        List<Long> udesIdInCacheList = userDTOList.stream()
                .map(UserDTO::getUserId)
                .collect(Collectors.toList());

        // 从 userIdList 中过滤出不在 udesIdInCacheList 中的用户 ID，生成 udesIdNotInCacheList
        List<Long> udesIdNotInCacheList = userIdList.stream()
                .filter(x -> !udesIdInCacheList.contains(x))
                .collect(Collectors.toList());

        // 将 udesIdNotInCacheList 中的每个 userId 按 userId % 100 分组，并将分组结果存储到 userIdMap 中
        Map<Long, List<Long>> userIdMap = udesIdNotInCacheList.stream()
                .collect(Collectors.groupingBy(userId -> userId % 100));

        // 创建一个线程安全的列表来存储数据库查询结果的 UserDTO 对象
        List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();

        // 并行处理每个分组的 userId 列表
        userIdMap.values().parallelStream().forEach(queryUserInList -> {
            // 从数据库中批量查询用户信息并将其转换为 UserDTO 对象后添加到 dbQueryResult 中
            dbQueryResult.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(queryUserInList), UserDTO.class));
        });

        // 如果数据库查询结果不为空，则将其批量保存到缓存中
        if (!CollectionUtils.isEmpty(dbQueryResult)) {
            // 创建一个 Map，其中 key 为缓存键，value 为对应的 UserDTO 对象
            Map<String, UserDTO> saveCacheMap = dbQueryResult.stream()
                    .collect(Collectors.toMap(
                            userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()),  // 生成缓存键
                            x -> x  // 使用 UserDTO 对象自身作为值
                    ));
            // 批量保存到 Redis 缓存中
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            // 将数据库查询结果添加到缓存结果列表中
            userDTOList.addAll(dbQueryResult);
        }

        // 将 userDTOList 转换为 Map，其中 key 为 userId，value 为对应的 UserDTO 对象
        return userDTOList.stream()
                .collect(Collectors.toMap(UserDTO::getUserId, x -> x));

    }


}

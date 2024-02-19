package top.whalefall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.whalefall.dto.Result;
import top.whalefall.dto.UserDTO;
import top.whalefall.entity.Follow;
import top.whalefall.entity.User;
import top.whalefall.mapper.FollowMapper;
import top.whalefall.service.IFollowService;
import org.springframework.stereotype.Service;
import top.whalefall.service.IUserService;
import top.whalefall.utils.UserHolder;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private IUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result follow(Long followUserId, Boolean ifFollow) {
        User byId = userService.getById(followUserId);
        if (byId == null) {
            return Result.fail("关注用户不存在");
        }
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;
        // 判断是关注还是取关
        if (ifFollow) {
            // 要关注
            // 存储到数据库
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSuccess = save(follow);
            // 保存到redis
            if (isSuccess) {
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        } else {
            // 取关 delete from tb_follow where userId = ? and follow_user_id = ?
            boolean isSuccess = remove(new QueryWrapper<Follow>()
                    .eq("user_id", userId)
                    .eq("follow_user_id", followUserId));
            if (isSuccess) {
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        User byId = userService.getById(followUserId);
        if (byId == null) {
            return Result.fail("关注用户不存在");
        }
        Long userId = UserHolder.getUser().getId();
        // 查询是否关注 select count(*) from tb_follow where userId = ? and follow_user_id = ?
        Long count = query()
                .eq("user_id", userId)
                .eq("follow_user_id", followUserId)
                .count();

        return Result.ok(count > 0);
    }

    @Override
    public Result followCommons(Long followUserId) {
        // 获取当前用户
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;
        String key2 = "follows:" + followUserId;

        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if (intersect == null || intersect.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());

        List<UserDTO> userDTOS = userService.listByIds(ids)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());

        return Result.ok(userDTOS);
    }
}

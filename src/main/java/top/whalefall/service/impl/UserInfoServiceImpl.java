package top.whalefall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.whalefall.entity.UserInfo;
import top.whalefall.mapper.UserInfoMapper;
import top.whalefall.service.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}

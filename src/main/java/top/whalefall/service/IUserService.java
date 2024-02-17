package top.whalefall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.whalefall.dto.LoginFormDTO;
import top.whalefall.dto.Result;
import top.whalefall.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone);

    Result login(LoginFormDTO loginForm);
}

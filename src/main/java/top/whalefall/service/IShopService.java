package top.whalefall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.whalefall.dto.Result;
import top.whalefall.entity.Shop;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IShopService extends IService<Shop> {

    Result queryById(Long id);

    Result update(Shop shop);
}

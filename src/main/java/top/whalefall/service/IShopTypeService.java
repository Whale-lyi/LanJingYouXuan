package top.whalefall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.whalefall.dto.Result;
import top.whalefall.entity.ShopType;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IShopTypeService extends IService<ShopType> {

    Result queryList();
}

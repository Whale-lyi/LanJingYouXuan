package top.whalefall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.whalefall.dto.Result;
import top.whalefall.entity.Voucher;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}

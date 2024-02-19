package top.whalefall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.whalefall.dto.Result;
import top.whalefall.entity.VoucherOrder;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);
}

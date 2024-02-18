package top.whalefall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.transaction.annotation.Transactional;
import top.whalefall.dto.Result;
import top.whalefall.entity.SeckillVoucher;
import top.whalefall.entity.VoucherOrder;
import top.whalefall.mapper.VoucherOrderMapper;
import top.whalefall.service.ISeckillVoucherService;
import top.whalefall.service.IVoucherOrderService;
import org.springframework.stereotype.Service;
import top.whalefall.utils.RedisIdWorker;
import top.whalefall.utils.UserHolder;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    public Result seckillVoucher(Long voucherId) {
        // 查询代金券
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // 判断是否开始或结束
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀尚未开始");
        }
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("秒杀已结束");
        }

        // 判断库存是否充足
        if (voucher.getStock() < 1) {
            return Result.fail("库存不足");
        }

        Long userId = UserHolder.getUser().getId();
        // 保证用户id一样时，使用的同一把锁
        // 保证先获取锁，再提交事务，再释放锁
        synchronized (userId.toString().intern()) {
            // 由于 @Transactional 是通过代理对象实现的，如果直接使用this.createVoucherOrder(), 事务是不生效的
            // 获取代理对象
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        }
    }

    @Transactional
    @Override
    public Result createVoucherOrder(Long voucherId) {
        // 一人一单
        Long userId = UserHolder.getUser().getId();
        // 查询订单
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            return Result.fail("用户已经购买过了");
        }

        // 扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId)
                .gt("stock", 0) // CAS解决超卖
                .update();
        if (!success) {
            return Result.fail("库存不足");
        }

        // 创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 订单id
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 用户id
        voucherOrder.setUserId(userId);
        // 代金券id
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);

        return Result.ok(orderId);
    }
}

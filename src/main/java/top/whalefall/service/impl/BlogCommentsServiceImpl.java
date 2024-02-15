package top.whalefall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.whalefall.entity.BlogComments;
import top.whalefall.mapper.BlogCommentsMapper;
import top.whalefall.service.IBlogCommentsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}

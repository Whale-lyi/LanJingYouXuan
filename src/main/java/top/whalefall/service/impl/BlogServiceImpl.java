package top.whalefall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.whalefall.entity.Blog;
import top.whalefall.mapper.BlogMapper;
import top.whalefall.service.IBlogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}

package top.xmlsj.signin.model.baidu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.model.baidu.domain.entity.BaiduUser;
import top.xmlsj.signin.model.baidu.domain.pojo.BaiduConfig;
import top.xmlsj.signin.model.baidu.mapper.BaiduUserMapper;
import top.xmlsj.signin.model.baidu.service.BaiduUserService;

import java.util.List;

/**
 * @author ForkManTou
 * @description 针对表【baidu_user】的数据库操作Service实现
 * @createDate 2023-04-08 14:07:30
 */
@Service
public class BaiduUserServiceImpl extends ServiceImpl<BaiduUserMapper, BaiduUser>
        implements BaiduUserService {

    /**
     * 数据初始
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void init() {
        // 读取配置
        BaiduConfig config = ConfigUtil.readBaiduConfig();
        // 读取用户数据
        List<BaiduUser> accounts = config.getAccounts();
        if (count() > 0) {
            accounts.forEach(u -> saveOrUpdate(u, new QueryWrapper<BaiduUser>().eq("name", u.getName())));
        } else {
            saveBatch(accounts);
        }
    }
}





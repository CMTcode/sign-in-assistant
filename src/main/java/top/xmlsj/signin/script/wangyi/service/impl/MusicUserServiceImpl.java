package top.xmlsj.signin.script.wangyi.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.script.wangyi.db.MusicUserMapper;
import top.xmlsj.signin.script.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.script.wangyi.domain.pojo.WangYiConfig;
import top.xmlsj.signin.script.wangyi.service.MusicUserService;

import java.util.List;


/**
 * @author CMT
 * @description 针对表【music_user】的数据库操作Service实现
 * @createDate 2022-11-04 20:29:11
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MusicUserServiceImpl extends ServiceImpl<MusicUserMapper, MusicUser> implements MusicUserService {


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void init() {
        // 读取配置
        WangYiConfig config = ConfigUtil.readWangYiConfig();
        // 读取用户数据
        List<MusicUser> accounts = config.getAccounts();
        if (count() > 0) {
            accounts.forEach(u -> saveOrUpdate(u, new QueryWrapper<MusicUser>().eq("name", u.getName())));
        } else {
            saveBatch(accounts);
        }
    }

    /**
     * 获取登录正常的用户
     *
     * @return
     */
    @Override
    public List<MusicUser> listNormal() {
        return list(new QueryWrapper<MusicUser>().eq("is_authenticated", 1));
    }
}





package top.xmlsj.signin.script.aliyundrive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.script.aliyundrive.db.AliyundriveUserMapper;
import top.xmlsj.signin.script.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.script.aliyundrive.domain.entity.AliYunDriveUser;
import top.xmlsj.signin.script.aliyundrive.service.AliyundriveUserService;

import java.util.List;

/**
 * @author ForkManTou
 * @description 针对表【aliyundrive_user】的数据库操作Service实现
 * @createDate 2023-03-25 16:44:50
 */
@Service
public class AliyundriveUserServiceImpl extends ServiceImpl<AliyundriveUserMapper, AliYunDriveUser>
        implements AliyundriveUserService {

    /**
     * 数据初始
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void init() {
        // 读取配置
        AliYunDriveConfig config = ConfigUtil.readAliYunDriveConfig();
        // 读取用户数据
        List<AliYunDriveUser> accounts = config.getAccounts();
        if (count() > 0) {
            getBaseMapper().truncateTable();
            saveBatch(accounts);
        } else {
            saveBatch(accounts);
        }
    }
}





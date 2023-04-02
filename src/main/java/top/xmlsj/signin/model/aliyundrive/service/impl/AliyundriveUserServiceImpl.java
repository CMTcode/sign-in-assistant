package top.xmlsj.signin.model.aliyundrive.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.aliyundrive.domain.entity.AliYunDriveUser;
import top.xmlsj.signin.model.aliyundrive.mapper.AliyundriveUserMapper;
import top.xmlsj.signin.model.aliyundrive.service.AliyundriveUserService;

/**
 * @author ForkManTou
 * @description 针对表【aliyundrive_user】的数据库操作Service实现
 * @createDate 2023-03-25 16:44:50
 */
@Service
public class AliyundriveUserServiceImpl extends ServiceImpl<AliyundriveUserMapper, AliYunDriveUser>
        implements AliyundriveUserService {

    @Override
    public void truncateTable() {
        baseMapper.truncateTable();
    }
}





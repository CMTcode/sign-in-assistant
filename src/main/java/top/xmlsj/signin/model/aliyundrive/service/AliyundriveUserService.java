package top.xmlsj.signin.model.aliyundrive.service;


import com.baomidou.mybatisplus.extension.service.IService;
import top.xmlsj.signin.model.aliyundrive.domain.entity.AliYunDriveUser;

/**
 * @author ForkManTou
 * @description 针对表【aliyundrive_user】的数据库操作Service
 * @createDate 2023-03-25 16:44:51
 */
public interface AliyundriveUserService extends IService<AliYunDriveUser> {
    void truncateTable();
}

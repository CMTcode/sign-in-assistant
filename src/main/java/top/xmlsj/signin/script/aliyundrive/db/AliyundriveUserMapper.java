package top.xmlsj.signin.script.aliyundrive.db;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.xmlsj.signin.script.aliyundrive.domain.entity.AliYunDriveUser;

/**
 * @author ForkManTou
 * @description 针对表【aliyundrive_user】的数据库操作Mapper
 * @createDate 2023-03-25 16:44:50
 * @Entity top.xmlsj.signin.aliyundrive.domain.AliyundriveUser
 */

@Mapper
public interface AliyundriveUserMapper extends BaseMapper<AliYunDriveUser> {
    void truncateTable();
}





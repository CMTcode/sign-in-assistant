package top.xmlsj.signin.script.baidu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xmlsj.signin.script.baidu.domain.entity.BaiduUser;

/**
 * @author ForkManTou
 * @description 针对表【baidu_user】的数据库操作Service
 * @createDate 2023-04-08 14:07:30
 */
public interface BaiduUserService extends IService<BaiduUser> {

    void init();

}

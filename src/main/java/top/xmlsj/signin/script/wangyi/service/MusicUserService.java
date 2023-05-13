package top.xmlsj.signin.script.wangyi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import top.xmlsj.signin.script.wangyi.domain.entity.MusicUser;

import java.util.List;


/**
 * @author CMT
 * @description 针对表【music_user】的数据库操作Service
 * @createDate 2022-11-04 20:29:11
 */
public interface MusicUserService extends IService<MusicUser> {

    void init();


    /**
     * 获取登录正常的用户
     *
     * @return
     */
    List<MusicUser> listNormal();

}

package top.xmlsj.signin.wangyi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import top.xmlsj.signin.wangyi.domain.entity.MusicUser;


/**
 * @author CMT
 * @description 针对表【music_user】的数据库操作Service
 * @createDate 2022-11-04 20:29:11
 */
public interface MusicUserService extends IService<MusicUser> {

    void truncateTable();
}

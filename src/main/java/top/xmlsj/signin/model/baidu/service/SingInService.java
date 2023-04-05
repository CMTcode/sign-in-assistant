package top.xmlsj.signin.model.baidu.service;


import top.xmlsj.signin.model.baidu.domain.entity.Account;
import top.xmlsj.signin.model.baidu.domain.pojo.forums.Forum;

import java.util.List;

/**
 * Created on 2022/10/31.
 * 百度签到服务
 *
 * @author Yang
 */
public interface SingInService {


    /**
     * 百度贴吧一键签到
     *
     * @param cooking
     * @return
     */
    void singIn(String cooking);

    /**
     * 签到
     *
     * @param account
     */
    void singIn(Account account);

    /**
     * 获取全部关注的贴吧列表
     *
     * @param bduss
     * @return
     */
    List<Forum> getForums(String bduss);

    void asyncSingIn(Account account);


    /**
     * 签到线程
     */
    void autoSingin();
}

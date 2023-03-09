package top.xmlsj.sigin.baidu.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.xmlsj.sigin.baidu.domain.entity.Account;
import top.xmlsj.sigin.baidu.domain.entity.BaiduConfig;
import top.xmlsj.sigin.baidu.domain.pojo.forums.Forum;
import top.xmlsj.sigin.baidu.domain.pojo.forums.Rows;
import top.xmlsj.sigin.baidu.service.SingInService;
import top.xmlsj.sigin.baidu.util.CookieUtil;
import top.xmlsj.sigin.baidu.util.TiebaSignUtil;
import top.xmlsj.sigin.util.YmlUtil;

import java.util.LinkedList;
import java.util.List;


/**
 * Created on 2022/10/31.
 *
 * @author Yang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ISingInServiceImpl implements SingInService {



    /**
     * 百度贴吧一键签到
     *
     * @param cooking
     * @return
     */
    @Override
    public void singIn(String cooking) {
        String bduss = CookieUtil.getBDUSS(cooking);
        //        对登录信息进行校验
        String tbstr = TiebaSignUtil.verification(bduss);
//        if (StringUtils.isBlank(tbstr)) {
//            mailSend.send("xxx@163.com", "百度贴吧签到", "cookie检测异常请检查！！！！");
//        }
        log.info("开始获取贴吧列表》》》》》》》》");
        List<Forum> forums = getForums(bduss);
        List<Forum> wForum = new LinkedList<>();
        log.info("获取到 {} 个贴吧，开始签到！！！》》》》", forums.size());
        forums.forEach(forum -> {
            aSingIn(forum, tbstr, bduss, wForum);
        });
        while (wForum.size() > 0) {
            log.info("签到失败{}个,开始重签》》》》》》》", wForum.size());
            wForum.forEach(forum -> {
                try {
                    Thread.sleep(RandomUtils.nextInt(500, 2000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                aSingIn(forum, tbstr, bduss, wForum);
            });
            wForum.clear();
        }
    }

    @Override
    public void singIn(Account account) {
        String tbstr = TiebaSignUtil.verification(account.getBduss());
        if (StringUtils.isBlank(tbstr)) {
            log.info("用户 {} 登录检查失败！请检查bduss", account.getName());
        } else {
            log.info(account.getName() + " : 开始获取贴吧列表》》》》》》》》");
            List<Forum> forums = getForums(account.getBduss());
            List<Forum> wForum = new LinkedList<>();
            log.info(account.getName() + " : 获取到 {} 个贴吧，开始签到！！！》》》》", forums.size());
            forums.forEach(forum -> {
                aSingIn(account.getName(), forum, tbstr, account.getBduss(), wForum);
            });
            while (wForum.size() > 0) {
                log.info(account.getName() + " : 签到失败{}个,开始重签》》》》》》》", wForum.size());
                wForum.forEach(forum -> {
                    try {
                        Thread.sleep(RandomUtils.nextInt(500, 2000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    aSingIn(account.getName(), forum, tbstr, account.getBduss(), wForum);
                });
                wForum.clear();
            }
        }
    }

    public void asyncSingIn(Account account) {
        String tbstr = TiebaSignUtil.verification(account.getBduss());
        if (StringUtils.isBlank(tbstr)) {
            log.info("用户 {} 登录检查失败！请检查bduss", account.getName());
        } else {
            log.info(account.getName() + " : 开始获取贴吧列表》》》》》》》》");
            List<Forum> forums = getForums(account.getBduss());
            List<Forum> wForum = new LinkedList<>();
            log.info(account.getName() + " : 获取到 {} 个贴吧，开始签到！！！》》》》", forums.size());
            forums.forEach(forum -> {
                aSingIn(account.getName(), forum, tbstr, account.getBduss(), wForum);
            });
            while (wForum.size() > 0) {
                log.info(account.getName() + " : 签到失败{}个,开始重签》》》》》》》", wForum.size());
                wForum.forEach(forum -> {
                    try {
                        Thread.sleep(RandomUtils.nextInt(500, 2000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    aSingIn(account.getName(), forum, tbstr, account.getBduss(), wForum);
                });
                wForum.clear();
            }
        }
    }

    void aSingIn(String name, Forum forum, String tbstr, String bduss, List<Forum> wForum) {
        String signResult = null;
        try {
            signResult = TiebaSignUtil.signForums(forum.getName(), forum.getId(), tbstr, bduss);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject result = (JSONObject) JSON.parse(signResult);
        if (signResult.contains("error_code") && signResult.contains("error_msg")) {
            String errorMsg = result.getString("error_msg");
            log.warn(name + " : 签到{}吧 失败！！: {}", forum.getName(), errorMsg);
            if (!errorMsg.contains("已经签过")) {
                wForum.add(forum);
            }
        } else {
            JSONObject userInfo = result.getJSONObject("user_info");
            log.info(name + " : 签到{}吧成功!经验+{},今天第{}个签到", forum.getName(), userInfo.getString("sign_bonus_point"), userInfo.getString("user_sign_rank"));
        }
    }

    void aSingIn(Forum forum, String tbstr, String bduss, List<Forum> wForum) {
        String signResult = null;
        try {
            signResult = TiebaSignUtil.signForums(forum.getName(), forum.getId(), tbstr, bduss);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject result = (JSONObject) JSON.parse(signResult);
        if (signResult.contains("error_code") && signResult.contains("error_msg")) {
            String errorMsg = result.getString("error_msg");
            log.warn("签到{}吧 失败！！: {}", forum.getName(), errorMsg);
            if (!errorMsg.contains("已经签过")) {
                wForum.add(forum);
            }
        } else {
            JSONObject userInfo = result.getJSONObject("user_info");
            log.info("签到{}吧成功!经验+{},今天第{}个签到", forum.getName(), userInfo.getString("sign_bonus_point"), userInfo.getString("user_sign_rank"));
        }
    }

    /**
     * 获取全部关注的贴吧列表
     *
     * @param bduss
     * @return
     */
    @Override
    public List<Forum> getForums(String bduss) {
        List<Forum> forums = new LinkedList<>();
        String forumsPage = null;
        try {
            forumsPage = TiebaSignUtil.getForums(bduss);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject result = (JSONObject) JSONObject.parse(forumsPage);
        Rows rows = result.toJavaObject(Rows.class);
        rows.getForumList().getGconforum().forEach(b -> {
            Forum forum = new Forum();
            BeanUtils.copyProperties(b, forum);
            forums.add(forum);
        });
        rows.getForumList().getNonGconforum().forEach(b -> {
            Forum forum = new Forum();
            BeanUtils.copyProperties(b, forum);
            forums.add(forum);
        });
        return forums;
    }


    @Override
    @Async("baiduasync")
    public void autoSingin() {
        log.info(".....................获取百度贴吧配置........................");
        BaiduConfig baiduConfig = YmlUtil.readConfigYml("./config/baidu.yml", BaiduConfig.class);
        if (baiduConfig.getEnabled()) {
            List<Account> accounts = baiduConfig.getAccounts();
            log.info("获取到{}个账号,开始签到", accounts.size());
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                log.info("签到第 {} 个账号 : {}", i + 1, account.getName());
                asyncSingIn(account);
            }
        } else {
            log.info("....................未启动贴吧签到.......................");
        }
    }
}

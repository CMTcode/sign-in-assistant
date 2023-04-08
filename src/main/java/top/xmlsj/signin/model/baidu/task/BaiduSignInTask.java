package top.xmlsj.signin.model.baidu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.baidu.domain.entity.BaiduUser;
import top.xmlsj.signin.model.baidu.domain.pojo.forums.Forum;
import top.xmlsj.signin.model.baidu.service.BaiduCoreService;
import top.xmlsj.signin.model.baidu.service.BaiduUserService;
import top.xmlsj.signin.model.baidu.util.TiebaSignUtil;
import top.xmlsj.signin.task.SigninTask;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 2023/4/8.
 * 关注贴吧签到
 *
 * @author Yang YaoWei
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaiduSignInTask implements SigninTask {


    private final BaiduUserService userService;
    private final BaiduCoreService coreService;

    private int status = 0;

    /**
     * 任务名
     *
     * @return taskName
     */
    @Override
    public String getName() {
        return "关注贴吧签到";
    }

    /**
     * 获取状态
     *
     * @return
     */
    @Override
    public int getState() {
        return status;
    }


    /**
     * 任务实现
     */
    @Override
    public void run() {
        // 获取有效用户
        List<BaiduUser> users = userService.list(new QueryWrapper<BaiduUser>().eq("is_authenticated", 1));
        for (BaiduUser user : users) {
            // 获取 tbs
            String tbstr = TiebaSignUtil.verification(user.getBduss());
            //获取关注贴吧
            log.info("------开始用户: {} 获取贴吧列表------", user.getName());
            List<Forum> forums = coreService.getForums(user.getBduss());
            if (forums.size() > 0) {
                //定义重签列表
                Set<Forum> reForum = new LinkedHashSet<>();
                log.info("用户: {} 获取到 {} 个贴吧，开始签到！！！", user.getName(), forums.size());
                forums.forEach(f -> log.info("name:{} |id:{}", f.getName(), f.getId()));
                forums.forEach(forum -> {
                    try {
                        Thread.sleep(RandomUtils.nextInt(0, 500));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    coreService.signin(user.getName(), forum, tbstr, user.getBduss(), reForum);
                });
                // 签到失败的进行重签
                while (reForum.size() > 0) {
                    log.info("签到失败{}个,开始进行重签", reForum.size());
                    reForum.forEach(f -> log.info("name:{} |id:{}", f.getName(), f.getId()));
                    reForum.forEach(f -> {
                        try {
                            Thread.sleep(RandomUtils.nextInt(500, 2000));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        coreService.signin(user.getName(), f, tbstr, user.getBduss(), reForum);
                    });
                    reForum.clear();
                }
            } else {
                log.info("用户 : {} 获取贴吧关注列表失败！！", user.getName());
            }
        }
    }


}

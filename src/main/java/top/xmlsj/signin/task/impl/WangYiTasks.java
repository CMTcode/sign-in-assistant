package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.model.wangyi.domain.pojo.WangYiConfig;
import top.xmlsj.signin.model.wangyi.service.MusicUserService;
import top.xmlsj.signin.model.wangyi.task.ListenToSongsTask;
import top.xmlsj.signin.model.wangyi.task.Signin;
import top.xmlsj.signin.model.wangyi.task.WangYiLoginCheckTask;
import top.xmlsj.signin.model.wangyi.task.YunBeiTask;
import top.xmlsj.signin.task.SignInTaskExecution;
import top.xmlsj.signin.task.SigninTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created on 2023/3/16.
 * 网易云音乐主任务
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WangYiTasks extends SignInTaskExecution {
    private final MusicUserService userService;

    private final YunBeiTask yunBeiTask;
    private final ListenToSongsTask listenToSongsTask;
    private final WangYiLoginCheckTask wangYiLoginCheckTask;
    private final Signin signin;



    @Async("wangyiasync")
    @Override
    public void runTask() {
        List<SigninTask> dailyTasks = new ArrayList<>();
        dailyTasks.add(yunBeiTask);
        dailyTasks.add(listenToSongsTask);
        dailyTasks.add(signin);
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, wangYiLoginCheckTask);
        log.info("获取网易云音配置中........................");
        WangYiConfig config = ConfigUtil.readWangYiConfig();
        userService.init();
        if (config.isEnabled()) {
            super.execute(dailyTasks);
        } else {
            log.info("网易云音乐 [未启用]");
        }
    }

}

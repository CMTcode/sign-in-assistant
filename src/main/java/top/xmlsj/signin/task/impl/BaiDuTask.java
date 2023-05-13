package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.script.baidu.domain.pojo.BaiduConfig;
import top.xmlsj.signin.script.baidu.task.BaiduLoginVerifyTask;
import top.xmlsj.signin.script.baidu.task.BaiduSignInTask;
import top.xmlsj.signin.task.SignInTaskExecution;
import top.xmlsj.signin.task.SigninTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2023/3/7.
 * 百度贴吧入口
 *
 * @author Yang YaoWei
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BaiDuTask extends SignInTaskExecution {

    private final BaiduLoginVerifyTask loginVerifyTask;
    private final BaiduSignInTask signInTask;


    @Override
    @Async("baiduasync")
    public void runTask() {
        List<SigninTask> dailyTasks = new ArrayList<>();
        dailyTasks.add(signInTask);
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, loginVerifyTask);
        log.info("获取百度贴吧配置中........................");
        BaiduConfig baiduConfig = ConfigUtil.readBaiduConfig();
        if (baiduConfig.isEnabled()) {
            execute(dailyTasks);
        } else {
            log.info("贴吧签到 [未启用]");
        }
    }

}

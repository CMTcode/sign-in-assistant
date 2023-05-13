package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.script.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.script.aliyundrive.task.AliYunDeriveSignTask;
import top.xmlsj.signin.script.aliyundrive.task.AliYunDriveCheckTask;
import top.xmlsj.signin.task.SignInTaskExecution;
import top.xmlsj.signin.task.SigninTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2023/3/25.
 * 阿里云网盘入口
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AliYunTask extends SignInTaskExecution {

    private final AliYunDeriveSignTask aliYunDeriveSignTask;
    private final AliYunDriveCheckTask aliYunDriveCheckTask;

    @Async("aliYunDeriveasync")
    @Override
    public void runTask() {
        List<SigninTask> dailyTasks;
        dailyTasks = new ArrayList<>();
        dailyTasks.add(aliYunDeriveSignTask);
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, aliYunDriveCheckTask);
        log.info("获取阿里云网盘配置中........................");
        AliYunDriveConfig config = ConfigUtil.readAliYunDriveConfig();
        if (config.isEnabled()) {
            execute(dailyTasks);
        } else {
            log.info("阿里云网盘 [未启用]");
        }
    }
}

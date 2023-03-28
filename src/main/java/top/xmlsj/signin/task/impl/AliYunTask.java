package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.aliyundrive.task.AliYunDriveTask;
import top.xmlsj.signin.aliyundrive.task.impl.AliYunDeriveSignTask;
import top.xmlsj.signin.aliyundrive.task.impl.AliYunDriveCheckTask;
import top.xmlsj.signin.util.CoreUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AliYunTask {

    private final AliYunDeriveSignTask aliYunDeriveSignTask;
    private final AliYunDriveCheckTask aliYunDriveCheckTask;

    @Async("aliYunDeriveasync")
    public void run() {
        List<AliYunDriveTask> dailyTasks;
        dailyTasks = new ArrayList<>();
        dailyTasks.add(aliYunDeriveSignTask);
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, aliYunDriveCheckTask);
        start(dailyTasks);
    }

    public void start(List<AliYunDriveTask> dailyTasks) {
        log.info("获取阿里云网盘配置中........................");
        AliYunDriveConfig config = CoreUtil.readAliYunDriveConfig();
        if (config.isEnabled()) {
            try {
                CoreUtil.printTime();
                log.debug("任务启动中");
                for (AliYunDriveTask task : dailyTasks) {
                    log.info("------{}开始------", task.getName());
                    try {
                        task.run();
                    } catch (Exception e) {
                        log.info("------{}--任务执行失败\n", task.getName());
                    }
                    log.info("------{}结束------\n", task.getName());
                    if ("登录检查".equals(task.getName()) && task.getState() == 1) {
                        break;
                    }
                    CoreUtil.taskSuspend();
                }
                log.info("本日任务已全部执行完毕");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("阿里云网盘 [未启用]");
        }

    }
}

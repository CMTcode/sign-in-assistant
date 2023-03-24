package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.util.CoreUtil;
import top.xmlsj.signin.wangyi.domain.entity.WangYiConfig;
import top.xmlsj.signin.wangyi.service.WangYiCoreService;
import top.xmlsj.signin.wangyi.task.Task;
import top.xmlsj.signin.wangyi.task.impl.ListenToSongsTask;
import top.xmlsj.signin.wangyi.task.impl.WangYiLoginCheckTask;
import top.xmlsj.signin.wangyi.task.impl.YunBeiTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.xmlsj.signin.bilbil.task.impl.TaskInfoHolder.calculateUpgradeDays;


/**
 * Created on 2023/3/16.
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WangYiTasks {

    private final YunBeiTask yunBeiTask;

    private final ListenToSongsTask listenToSongsTask;

    private final WangYiCoreService wangYiCoreService;
    private final WangYiLoginCheckTask wangYiLoginCheckTask;


    @Async("wangyiasync")
    public void run() {
        List<Task> dailyTasks;
        dailyTasks = new ArrayList<>();
        dailyTasks.add(yunBeiTask);
        dailyTasks.add(listenToSongsTask);
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, wangYiLoginCheckTask);
        start(dailyTasks);
    }


    public void start(List<Task> dailyTasks) {
        log.info("获取网易云音配置中........................");
        WangYiConfig config = wangYiCoreService.readWangYiConfig();
//        Assert.isTrue(config.isEnabled(),"未开启网易云音乐签到");
        if (config.isEnabled()) {
            try {
                CoreUtil.printTime();
                log.debug("任务启动中");
                for (Task task : dailyTasks) {
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
                calculateUpgradeDays();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("网易云音乐 [未启用]");
        }

    }

}

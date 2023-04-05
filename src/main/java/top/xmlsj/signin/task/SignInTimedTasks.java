package top.xmlsj.signin.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.task.impl.AliYunTask;
import top.xmlsj.signin.task.impl.BaiDuTask;
import top.xmlsj.signin.task.impl.BilBilTasks;
import top.xmlsj.signin.task.impl.WangYiTasks;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class SignInTimedTasks {

    private final BaiDuTask baiDuTask;
    private final BilBilTasks BilBilTasks;
    private final WangYiTasks wangYiTasks;
    private final AliYunTask aliYunTask;

    /**
     * 运行一次所有项目
     */
    public void start() {
        baiDuTask.runTask();
        BilBilTasks.runTask();
        wangYiTasks.runTask();
        aliYunTask.runTask();
    }

    @Scheduled(cron = "1 1 0 * * ?")
    @Async("bilbilasync")
    public void bilbilTimer() {
        log.info("开始哔哩哔哩每日定时任务");
        BilBilTasks.runTask();
    }

    @Scheduled(cron = "1 1 0 * * ?")
    @Async("baiduasync")
    public void baiduTimer() {
        log.info("开始百度贴吧每日定时任务");
        baiDuTask.runTask();
    }

    @Scheduled(cron = "1 1 0 * * ?")
    @Async("wangyiasync")
    public void wangyiTimer() {
        log.info("开始网易每日定时任务");
        wangYiTasks.runTask();
    }

    @Scheduled(cron = "1 1 0 * * ?")
    @Async("aliYunDeriveasync")
    public void aliYunTimer() {
        log.info("开始阿里云网盘每日定时任务");
        aliYunTask.runTask();
    }
}

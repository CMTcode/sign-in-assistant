package top.xmlsj.sigin.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created on 2023/3/9.
 *
 * @author Yang YaoWei
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TimedTasks {

    private final BaiDuTask baiDuTask;

    private final BilBilTask bilBilTask;

    @Scheduled(cron = "1 1 0 * * ?")
    @Async("bilbilasync")
    public void bilbilTimer() {
        log.info("开始哔哩哔哩每日定时任务");
        bilBilTask.run();
    }

    @Scheduled(cron = "1 1 0 * * ?")
    @Async("baiduasync")
    public void baiduTimer() {
        log.info("开始百度贴吧每日定时任务");
        baiDuTask.run();
    }
}

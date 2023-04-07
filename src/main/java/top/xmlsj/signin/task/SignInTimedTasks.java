package top.xmlsj.signin.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.core.config.YmlPropertySourceFactory;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2023/3/7.
 * 任务执行主线
 *
 * @author Yang YaoWei
 */

@Slf4j
@Component
@PropertySource(value = "file:config/appconfig.yml",
        factory = YmlPropertySourceFactory.class)
public class SignInTimedTasks {

    @Resource
    private final Map<String, SignInTaskExecution> signTasks = new ConcurrentHashMap<>();

    /**
     * 运行一次所有项目
     */
    public void start() {
        signTasks.forEach((k, v) -> v.runTask());
    }

    @Scheduled(cron = "${scheduled.cron}")
    public void bilbilTimer() {
        log.info("开始执行每日定时任务");
        start();
    }

}

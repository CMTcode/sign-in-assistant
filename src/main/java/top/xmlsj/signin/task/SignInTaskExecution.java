package top.xmlsj.signin.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.core.util.CoreUtil;

import java.util.List;

/**
 * Created on 2023/4/5.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public abstract class SignInTaskExecution {

    protected final void execute(List<SigninTask> tasks) {
        try {
            CoreUtil.printTime();
            log.debug("任务启动中");
            for (SigninTask task : tasks) {
                log.info("------{}开始------", task.getName());
                try {
                    task.run();
                } catch (Exception e) {
                    log.info("------{}--任务执行失败\n", task.getName());
                    log.warn("任务 {}执行异常 : {}", task.getName(), e.getMessage());
                }
                log.info("------{}结束------\n", task.getName());
                if ("登录检查".equals(task.getName()) && task.getState() == 0) {
                    break;
                }
                CoreUtil.taskSuspend();
            }
            log.info("本日任务已全部执行完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void runTask();
}

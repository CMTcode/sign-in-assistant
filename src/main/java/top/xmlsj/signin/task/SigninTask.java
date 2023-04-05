package top.xmlsj.signin.task;

/**
 * Created on 2023/3/16.
 *
 * @author Yang YaoWei
 */
public interface SigninTask {

    /**
     * 任务实现
     */
    void run();

    /**
     * 任务名
     *
     * @return taskName
     */
    String getName();

    /**
     * 获取状态
     *
     * @return
     */
    int getState();
}

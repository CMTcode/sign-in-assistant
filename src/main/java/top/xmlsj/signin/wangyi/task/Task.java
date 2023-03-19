package top.xmlsj.signin.wangyi.task;

/**
 * Created on 2023/3/16.
 *
 * @author Yang YaoWei
 */
public interface Task {

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

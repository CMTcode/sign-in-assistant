package top.xmlsj.signin.model.aliyundrive.task;

/**
 * Created on 2023/3/16.
 * 阿里云
 *
 * @author Yang YaoWei
 */
public interface AliYunDriveTask {

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

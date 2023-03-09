package top.xmlsj.sigin.bilbil.task;

import lombok.Data;

/**
 * @author ForkManTou
 * @since 2020-11-22 5:22
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

    int getState();

}

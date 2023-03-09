package top.xmlsj.sigin.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Component
@RequiredArgsConstructor
public class MainTask {

    private final BaiDuTask baiDuTask;

    private final BilBilTask bilBilTask;

    public void run() {
        baiDuTask.run();
        bilBilTask.run();
    }
}

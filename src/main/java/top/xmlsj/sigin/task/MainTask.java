package top.xmlsj.sigin.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.xmlsj.sigin.baidu.service.SingInService;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Component
@RequiredArgsConstructor
public class MainTask {

    private final SingInService baiduService;
    public void run() {
        baiduService.autoSingin();
    }
}

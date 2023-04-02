package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.model.baidu.service.SingInService;
import top.xmlsj.signin.task.SignInTask;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Component
@RequiredArgsConstructor
public class BaiDuTask implements SignInTask {

    private final SingInService baiduService;


    @Override
    public void run() {
        baiduService.autoSingin();
    }

}

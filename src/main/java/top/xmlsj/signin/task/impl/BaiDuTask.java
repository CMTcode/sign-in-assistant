package top.xmlsj.signin.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.model.baidu.domain.entity.Account;
import top.xmlsj.signin.model.baidu.domain.entity.BaiduConfig;
import top.xmlsj.signin.model.baidu.service.SingInService;
import top.xmlsj.signin.task.SignInTaskExecution;
import top.xmlsj.signin.util.CoreUtil;

import java.util.List;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BaiDuTask extends SignInTaskExecution {

    private final SingInService baiduService;


    @Override
    @Async("baiduasync")
    public void runTask() {
        log.info("获取百度贴吧配置中........................");
        BaiduConfig baiduConfig = CoreUtil.readBaiduConfig();
        if (baiduConfig.getEnabled()) {
            List<Account> accounts = baiduConfig.getAccounts();
            log.info("获取到{}个账号,开始签到", accounts.size());
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                log.info("签到第 {} 个账号 : {}", i + 1, account.getName());
                baiduService.asyncSingIn(account);
            }
        } else {
            log.info("贴吧签到 [未启用]");
        }
    }

}

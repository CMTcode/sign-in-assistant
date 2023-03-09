package top.xmlsj.sigin.task;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.sigin.bilbil.config.Config;
import top.xmlsj.sigin.bilbil.domain.entity.Account;
import top.xmlsj.sigin.bilbil.domain.entity.BilBilConfig;
import top.xmlsj.sigin.bilbil.domain.login.Verify;
import top.xmlsj.sigin.bilbil.task.DailyTask;
import top.xmlsj.sigin.util.YmlUtil;

import java.util.List;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BilBilTask {

    private final DailyTask dailyTask;
    @Async("bilbilasunc")
    public void run() {
        this.sigin();
    }


    public void sigin() {
        log.info(".....................获取哔哩哔哩配置........................");
        BilBilConfig bilBilConfig = YmlUtil.readConfigYml("./config/bilbil.yml", BilBilConfig.class);
        if (bilBilConfig.getEnabled()) {
            List<Account> accounts = bilBilConfig.getAccounts();
            log.info("获取到{}个账号,开始签到", accounts.size());
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                log.info("签到第 {} 个账号 : {}", i + 1, account.getName());
                Verify.verifyInit(account.getDEDEUSERID(), account.getSESSDATA(), account.getBILI_JCT());
                //配置签到信息
                Config.chargeForLove = String.valueOf(account.getChargeForLove());
                Config.reserveCoins = account.getReserveCoins();
                Config.upLive = String.valueOf(account.getUpLive());
                //获取完整的浏览器 UA
                Config.userAgent = account.getUserAgent();
                dailyTask.doDailyTask();
            }
        } else {
            log.info("....................未启动哔哩哔哩签到.......................");
        }
    }
}

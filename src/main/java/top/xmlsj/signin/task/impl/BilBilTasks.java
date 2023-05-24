package top.xmlsj.signin.task.impl;


import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.script.bilbil.apiquery.ApiList;
import top.xmlsj.signin.script.bilbil.config.Config;
import top.xmlsj.signin.script.bilbil.domain.entity.Account;
import top.xmlsj.signin.script.bilbil.domain.entity.BilBilConfig;
import top.xmlsj.signin.script.bilbil.domain.login.Verify;
import top.xmlsj.signin.script.bilbil.task.impl.*;
import top.xmlsj.signin.script.bilbil.utils.HttpUtil;
import top.xmlsj.signin.task.SignInTaskExecution;
import top.xmlsj.signin.task.SigninTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.xmlsj.signin.script.bilbil.task.impl.TaskInfoHolder.STATUS_CODE_STR;

/**
 * Created on 2023/3/7.
 * 哔哩哔哩主任务
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BilBilTasks extends SignInTaskExecution {


    /**
     * @return jsonObject 返回status对象，包含{"login":true,"watch":true,"coins":50,
     * "share":true,"email":true,"tel":true,"safe_question":true,"identify_card":false}
     * @author @srcrs
     */
    public static JsonObject getDailyTaskStatus() {
        JsonObject jsonObject = HttpUtil.doGet(ApiList.reward);
        int responseCode = jsonObject.get(STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            log.info("请求本日任务完成状态成功");
            return jsonObject.get("data").getAsJsonObject();
        } else {
            log.debug(jsonObject.get("message").getAsString());
            return HttpUtil.doGet(ApiList.reward).get("data").getAsJsonObject();
            //偶发性请求失败，再请求一次。
        }
    }

    @Async("bilbilasync")
    @Override
    public void runTask() {
        log.info("获取哔哩哔哩配置中........................");
        BilBilConfig bilBilConfig = ConfigUtil.readBilBilConfig();
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
                List<SigninTask> dailyTasks;
                dailyTasks = new ArrayList<>();
                // TODO 暂时关闭待重写哔哩哔哩任务
                dailyTasks.add(new VideoWatch());
                dailyTasks.add(new CoinAdd());
                dailyTasks.add(new MangaSign());
                dailyTasks.add(new MangaRead());
                dailyTasks.add(new LiveCheckin());
                dailyTasks.add(new GiveGift());
                dailyTasks.add(new ChargeMe());
                dailyTasks.add(new GetVipPrivilege());
                Collections.shuffle(dailyTasks);
                dailyTasks.add(0, new UserCheck());
                super.execute(dailyTasks);
            }
        } else {
            log.info("哔哩哔哩签到 [未启用] ");
        }
    }

}

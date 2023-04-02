package top.xmlsj.signin.task.impl;


import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.model.bilbil.apiquery.ApiList;
import top.xmlsj.signin.model.bilbil.config.Config;
import top.xmlsj.signin.model.bilbil.domain.entity.Account;
import top.xmlsj.signin.model.bilbil.domain.entity.BilBilConfig;
import top.xmlsj.signin.model.bilbil.domain.login.Verify;
import top.xmlsj.signin.model.bilbil.task.Task;
import top.xmlsj.signin.model.bilbil.task.impl.*;
import top.xmlsj.signin.model.bilbil.utils.HttpUtil;
import top.xmlsj.signin.util.CoreUtil;
import top.xmlsj.signin.util.YmlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.xmlsj.signin.model.bilbil.task.impl.TaskInfoHolder.STATUS_CODE_STR;
import static top.xmlsj.signin.model.bilbil.task.impl.TaskInfoHolder.calculateUpgradeDays;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BilBilTasks {


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
    public void run() {
        log.info("获取哔哩哔哩配置中........................");
        BilBilConfig bilBilConfig = YmlUtil.readConfig("./config/bilbil.yml", BilBilConfig.class);
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
//                dailyTask.doDailyTask();
                start();
            }
        } else {
            log.info("哔哩哔哩签到 [未启用] ");
        }
    }

    void start() {
        List<Task> dailyTasks;
        dailyTasks = new ArrayList<>();
        dailyTasks.add(new VideoWatch());
        dailyTasks.add(new MangaSign());
        dailyTasks.add(new CoinAdd());
//        dailyTasks.add(new Silver2coin());
        dailyTasks.add(new LiveCheckin());
        dailyTasks.add(new GiveGift());
        dailyTasks.add(new ChargeMe());
        dailyTasks.add(new GetVipPrivilege());
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, new UserCheck());
        doDailyTask(dailyTasks);
    }

    public void doDailyTask(List<Task> dailyTasks) {
        try {
            CoreUtil.printTime();
            log.debug("任务启动中");
            for (Task task : dailyTasks) {
                log.info("------{}开始------", task.getName());
                try {
                    task.run();
                } catch (Exception e) {
                    log.info("------{}--任务执行失败\n", task.getName());
                    log.warn("任务 {}执行异常 : {}", task.getName(), e.getMessage());
                }
                log.info("------{}结束------\n", task.getName());
                if ("登录检查".equals(task.getName()) && task.getState() == 1) {
                    break;
                }
                CoreUtil.taskSuspend();
            }
            log.info("本日任务已全部执行完毕");
            calculateUpgradeDays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

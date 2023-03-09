package top.xmlsj.sigin.bilbil.task;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.xmlsj.sigin.bilbil.apiquery.ApiList;
import top.xmlsj.sigin.bilbil.utils.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.*;

import static top.xmlsj.sigin.bilbil.task.TaskInfoHolder.STATUS_CODE_STR;
import static top.xmlsj.sigin.bilbil.task.TaskInfoHolder.calculateUpgradeDays;


/**
 * @author ForkManTou
 * @create 2020/10/11 20:44
 */
@Slf4j
@Component
public class DailyTask {

    private final List<Task> dailyTasks;


    public DailyTask() {
        dailyTasks = new ArrayList<>();
        dailyTasks.add(new VideoWatch());
        dailyTasks.add(new MangaSign());
        dailyTasks.add(new CoinAdd());
        dailyTasks.add(new Silver2coin());
        dailyTasks.add(new LiveCheckin());
        dailyTasks.add(new GiveGift());
        dailyTasks.add(new ChargeMe());
        dailyTasks.add(new GetVipPrivilege());
        Collections.shuffle(dailyTasks);
        dailyTasks.add(0, new UserCheck());
    }

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

    public void doDailyTask() {
        try {
            printTime();
            log.debug("任务启动中");
            for (Task task : dailyTasks) {
                log.info("------{}开始------", task.getName());
                try {
                    task.run();
                } catch (Exception e) {
                    log.info("------{}--任务执行失败\n", task.getName());
                }
                log.info("------{}结束------\n", task.getName());
                if ("登录检查".equals(task.getName()) && task.getState() == 1) {
                    break;
                }
                taskSuspend();
            }
            log.info("本日任务已全部执行完毕");
            calculateUpgradeDays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        log.info(time);
    }

    private void taskSuspend() throws InterruptedException {
        Random random = new Random();
        int sleepTime = (int) ((random.nextDouble() + 0.5) * 3000);
        log.info("-----随机暂停{}ms-----\n", sleepTime);
        Thread.sleep(sleepTime);
    }

}


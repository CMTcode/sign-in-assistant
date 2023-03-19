package top.xmlsj.signin.bilbil.task.impl;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import top.xmlsj.signin.bilbil.apiquery.ApiList;
import top.xmlsj.signin.bilbil.task.Task;
import top.xmlsj.signin.bilbil.utils.HttpUtil;

import static top.xmlsj.signin.bilbil.task.impl.TaskInfoHolder.STATUS_CODE_STR;

/**
 * 直播签到
 *
 * @author ForkManTou
 * @since 2020-11-22 5:42
 */
@Slf4j
public class LiveCheckin implements Task {


    private final String taskName = "直播签到";

    @Override
    public void run() {
        JsonObject liveCheckinResponse = HttpUtil.doGet(ApiList.liveCheckin);
        int code = liveCheckinResponse.get(STATUS_CODE_STR).getAsInt();
        if (code == 0) {
            JsonObject data = liveCheckinResponse.get("data").getAsJsonObject();
            log.info("直播签到成功，本次签到获得" + data.get("text").getAsString() + "," + data.get("specialText").getAsString());
        } else {
            String message = liveCheckinResponse.get("message").getAsString();
            log.debug("直播签到失败: " + message);
        }
    }

    @Override
    public String getName() {
        return taskName;
    }

    @Override
    public int getState() {
        return 0;
    }
}

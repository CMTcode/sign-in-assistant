package top.xmlsj.signin.model.bilbil.task.impl;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import top.xmlsj.signin.model.bilbil.apiquery.ApiList;
import top.xmlsj.signin.model.bilbil.apiquery.oftenAPI;
import top.xmlsj.signin.model.bilbil.domain.login.Verify;
import top.xmlsj.signin.model.bilbil.utils.HttpUtil;
import top.xmlsj.signin.task.SigninTask;

import java.util.Random;

import static top.xmlsj.signin.task.impl.BilBilTasks.getDailyTaskStatus;

/**
 * 观看分享视频
 *
 * @author ForkManTou
 * @since 2020-11-22 5:13
 */
@Slf4j
public class VideoWatch implements SigninTask {

    private final String taskName = "观看分享视频";

    @Override
    public void run() {

        JsonObject dailyTaskStatus = getDailyTaskStatus();
        String bvid = TaskInfoHolder.getVideoId.getRegionRankingVideoBvid();
        if (!dailyTaskStatus.get("watch").getAsBoolean()) {
            watchVideo(bvid);
        } else {
            log.info("本日观看视频任务已经完成了，不需要再观看视频了");
        }

        if (!dailyTaskStatus.get("share").getAsBoolean()) {
            dailyAvShare(bvid);
        } else {
            log.info("本日分享视频任务已经完成了，不需要再分享视频了");
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

    public void watchVideo(String bvid) {
        int playedTime = new Random().nextInt(90) + 1;
        String postBody = "bvid=" + bvid
                + "&played_time=" + playedTime;
        JsonObject resultJson = HttpUtil.doPost(ApiList.videoHeartbeat, postBody);
        String videoTitle = oftenAPI.videoTitle(bvid);
        int responseCode = resultJson.get(TaskInfoHolder.STATUS_CODE_STR).getAsInt();
        if (responseCode == 0) {
            log.info("视频: " + videoTitle + "播放成功,已观看到第" + playedTime + "秒");
        } else {
            log.debug("视频: " + videoTitle + "播放失败,原因: " + resultJson.get("message").getAsString());
        }
    }

    /**
     * @param bvid 要分享的视频bvid
     */
    public void dailyAvShare(String bvid) {
        String requestBody = "bvid=" + bvid + "&csrf=" + Verify.getInstance().getBiliJct();
        JsonObject result = HttpUtil.doPost((ApiList.AvShare), requestBody);

        String videoTitle = oftenAPI.videoTitle(bvid);

        if (result.get(TaskInfoHolder.STATUS_CODE_STR).getAsInt() == 0) {
            log.info("视频: " + videoTitle + " 分享成功");
        } else {
            log.debug("视频分享失败，原因: " + result.get("message").getAsString());
            log.debug("开发者提示: 如果是csrf校验失败请检查BILI_JCT参数是否正确或者失效");
        }
    }
}

package top.xmlsj.signin.script.bilbil.task.impl;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.script.bilbil.apiquery.ApiList;
import top.xmlsj.signin.script.bilbil.config.Config;
import top.xmlsj.signin.script.bilbil.utils.HttpUtil;
import top.xmlsj.signin.task.SigninTask;


/**
 * 漫画签到
 *
 * @author ForkManTou
 * @since 2020-11-22 5:22
 */

@Slf4j
@Service
public class MangaSign implements SigninTask {


    private final String taskName = "漫画签到";

    @Override
    public void run() {

        String platform = Config.devicePlatform;
        String requestBody = "platform=" + platform;
        JsonObject result = HttpUtil.doPost(ApiList.Manga, requestBody);

        if (result == null) {
            log.info("哔哩哔哩漫画已经签到过了");
        } else {
            log.info("完成漫画签到");
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

package top.xmlsj.signin.model.bilbil.task.impl;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import top.xmlsj.signin.model.bilbil.apiquery.ApiList;
import top.xmlsj.signin.model.bilbil.utils.HttpUtil;
import top.xmlsj.signin.task.SigninTask;

/**
 * @author ForkManTou
 * @create 2021/1/13 17:50
 */
@Slf4j
public class MangaRead implements SigninTask {

    @Override
    public void run() {
        String urlParam = "?device=pc&platform=web";
        String requestBody = "comic_id=27355" +
                "&ep_id=381662";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device", "pc");
        jsonObject.addProperty("platform", "web");
        jsonObject.addProperty("comic_id", "27355");
        jsonObject.addProperty("ep_id", "381662");

        JsonObject result = HttpUtil.doPost(ApiList.mangaRead + urlParam, jsonObject);
        int code = result.get(TaskInfoHolder.STATUS_CODE_STR).getAsInt();
        if (code == 0) {
            log.info("本日漫画自动阅读1章节成功！，阅读漫画为：堀与宫村");
        } else {
            log.debug("阅读失败,错误信息为\n```json\n{}\n```", result);
        }

    }

    @Override
    public String getName() {
        return "每日漫画阅读";
    }

    @Override
    public int getState() {
        return 0;
    }
}

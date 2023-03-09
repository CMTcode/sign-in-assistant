package top.xmlsj.sigin.bilbil.task;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.xmlsj.sigin.bilbil.apiquery.ApiList;
import top.xmlsj.sigin.bilbil.domain.pojo.userinfobean.Data;
import top.xmlsj.sigin.bilbil.utils.HelpUtil;
import top.xmlsj.sigin.bilbil.utils.HttpUtil;


import javax.annotation.Resource;

import static top.xmlsj.sigin.bilbil.task.TaskInfoHolder.STATUS_CODE_STR;
import static top.xmlsj.sigin.bilbil.task.TaskInfoHolder.userInfo;

/**
 * 登录检查
 *
 * @author ForkManTou
 * @since 2020-11-22 4:57
 */
@Slf4j
@Component
public class UserCheck implements Task {

    private final String taskName = "登录检查";

    private int STATE = 0;



    @Override
    public void run() {
        String requestPram = "";
        JsonObject userJson = HttpUtil.doGet(ApiList.LOGIN + requestPram);
        if (userJson == null) {
            log.info("用户信息请求失败，如果是412错误，请在config.json中更换UA，412问题仅影响用户信息确认，不影响任务");
        } else {
            userJson = HttpUtil.doGet(ApiList.LOGIN);
            //判断Cookies是否有效
            if (userJson.get(STATUS_CODE_STR).getAsInt() == 0
                    && userJson.get("data").getAsJsonObject().get("isLogin").getAsBoolean()) {
                userInfo = new Gson().fromJson(userJson
                        .getAsJsonObject("data"), Data.class);
                log.info("Cookies有效，登录成功");
                log.info("用户名称: {}", HelpUtil.userNameEncode(userInfo.getUname()));
                log.info("硬币余额: " + userInfo.getMoney());
            } else {
                log.debug(String.valueOf(userJson));
                log.warn("Cookies可能失效了,请仔细检查DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
                this.STATE = 1;
//                mailSend.send("哔哩哔哩异常", "Cookies可能失效了,请仔细检查Github Secrets中DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
            }
        }

    }

    @Override
    public String getName() {
        return taskName;
    }

    @Override
    public int getState() {
        return STATE;
    }
}

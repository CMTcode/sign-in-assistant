package top.xmlsj.signin.script.bilbil.task.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.model.message.service.SendService;
import top.xmlsj.signin.script.bilbil.apiquery.ApiList;
import top.xmlsj.signin.script.bilbil.domain.pojo.userinfobean.BilBilUserData;
import top.xmlsj.signin.script.bilbil.utils.HelpUtil;
import top.xmlsj.signin.script.bilbil.utils.HttpUtil;
import top.xmlsj.signin.task.SigninTask;


/**
 * 登录检查
 *
 * @author ForkManTou
 * @since 2020-11-22 4:57
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCheck implements SigninTask {

    private final String taskName = "登录检查";

    private int STATE = 0;

    @Autowired
    private SendService sendService;


    @Override
    public void run() {
        String requestPram = "";
        JsonObject userJson = HttpUtil.doGet(ApiList.LOGIN + requestPram);
        if (userJson == null) {
            log.info("用户信息请求失败，如果是412错误，请在bilbil.yml中更换UA，412问题仅影响用户信息确认，不影响任务");
        } else {
            userJson = HttpUtil.doGet(ApiList.LOGIN);
            //判断Cookies是否有效
            if (userJson.get(TaskInfoHolder.STATUS_CODE_STR).getAsInt() == 0
                    && userJson.get("data").getAsJsonObject().get("isLogin").getAsBoolean()) {
                TaskInfoHolder.userInfo = new Gson().fromJson(userJson
                        .getAsJsonObject("data"), BilBilUserData.class);
                log.info("Cookies有效，登录成功");
                log.info("用户名称: {}", HelpUtil.userNameEncode(TaskInfoHolder.userInfo.getUname()));
                log.info("硬币余额: " + TaskInfoHolder.userInfo.getMoney());
                this.STATE = 1;
            } else {
                log.debug(String.valueOf(userJson));
                log.warn("Cookies可能失效了,请仔细检查DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
//                mailSend.send("哔哩哔哩异常", "Cookies可能失效了,请仔细检查Github Secrets中DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
                sendService.send("哔哩哔哩 : 用户 " + TaskInfoHolder.userInfo.getUname() + "Cookies可能失效了,请仔细检查Github Secrets中DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
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

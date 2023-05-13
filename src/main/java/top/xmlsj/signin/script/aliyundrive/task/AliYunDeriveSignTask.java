package top.xmlsj.signin.script.aliyundrive.task;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.script.aliyundrive.constant.AliYunConst;
import top.xmlsj.signin.script.aliyundrive.domain.entity.AliYunDriveUser;
import top.xmlsj.signin.script.aliyundrive.domain.pojo.aliyunsigninfo.AliYunSignInfo;
import top.xmlsj.signin.script.aliyundrive.domain.pojo.aliyunsigninfo.Reward;
import top.xmlsj.signin.script.aliyundrive.domain.pojo.aliyunsigninfo.SignInLog;
import top.xmlsj.signin.script.aliyundrive.domain.pojo.aliyunsigninfo.Status;
import top.xmlsj.signin.script.aliyundrive.service.AliyundriveUserService;
import top.xmlsj.signin.task.SigninTask;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@Slf4j
@Service
public class AliYunDeriveSignTask implements SigninTask {

    private static final String NAME = "每日签到";

    @Resource
    private AliyundriveUserService userService;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        List<AliYunDriveUser> users = userService.list();
        users.forEach(u -> {
            if (u.getIsAuthenticated() == 1) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("_rx-s", "mobile");
                HashMap<String, Object> body = new HashMap<>();
                body.put("isReward", true);
                String result = HttpRequest.post(AliYunConst.SIGNIN_IN_URL)
                        .header(Header.AUTHORIZATION, u.getToken())
                        .form(params)
                        .body(JSON.toJSONString(body)).execute().body();
                AliYunSignInfo aliYunSignInfo = JSONObject.parseObject(result, AliYunSignInfo.class);
                List<SignInLog> signInLogs = aliYunSignInfo.getResult().getSignInLogs();
                Collections.reverse(signInLogs);
                Reward reward = null;
                for (SignInLog signInLog : signInLogs) {
                    if (signInLog.getStatus().equals(Status.NORMAL)) {
                        reward = signInLog.getReward();
                    }
                }
                log.info("签到成功,获得奖励 : [{} -> {} -> {}]", Objects.requireNonNull(reward).getName(), reward.getDescription(), reward.getNotice());
                log.info("已签到 {} 天", aliYunSignInfo.getResult().getSignInCount());
            }
        });
    }

    /**
     * 任务名
     *
     * @return taskName
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 获取状态
     *
     * @return
     */
    @Override
    public int getState() {
        return 0;
    }
}

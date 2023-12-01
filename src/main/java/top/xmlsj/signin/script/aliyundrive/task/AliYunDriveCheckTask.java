package top.xmlsj.signin.script.aliyundrive.task;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.core.util.ExceptionConstants;
import top.xmlsj.signin.model.message.service.SendService;
import top.xmlsj.signin.script.aliyundrive.constant.AliYunConst;
import top.xmlsj.signin.script.aliyundrive.domain.entity.AliYunDriveUser;
import top.xmlsj.signin.script.aliyundrive.service.AliyundriveUserService;
import top.xmlsj.signin.task.SigninTask;

import java.util.HashMap;
import java.util.List;

/**
 * Created on 2023/3/25.
 * 登录
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AliYunDriveCheckTask implements SigninTask {

    private static final String NAME = "登录检查";

    private final AliyundriveUserService userService;

    private final SendService sendService;

    private int STATE = 0;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        List<AliYunDriveUser> accounts = userService.list();
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        accounts.forEach(u -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("refresh_token", u.getRefreshToken());
            String post = HttpRequest.post(AliYunConst.TOKEN_URL)
                    .body(JSON.toJSONString(params))
                    .execute().body();
            JSONObject parseObject = JSONObject.parseObject(post);
            if (post.contains("access_token")) {
                String accessToken = parseObject.getString("access_token");
                String tokenType = parseObject.getString("token_type");
                u.setToken(tokenType + " " + accessToken);
                u.setIsAuthenticated(1);
                userService.updateById(u);
            } else {
                log.info("用户 {} 登录验证错误请检查refreshToken！！！", u.getName());
                sendService.send("阿里云网盘 : 用户 " + u.getName() + " 登录验证错误请检查refreshToken！！！");
            }
        });
        long auths = userService.count(new QueryWrapper<AliYunDriveUser>().eq("is_authenticated", 1));
        if (auths > 0) {
            // 如果无通过登录检测用户 服状态码为0 后面项目不执行
            this.STATE = 1;
        }
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
        return STATE;
    }
}

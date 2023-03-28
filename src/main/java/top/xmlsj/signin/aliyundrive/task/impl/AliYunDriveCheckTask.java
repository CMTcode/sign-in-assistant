package top.xmlsj.signin.aliyundrive.task.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.aliyundrive.domain.entity.AliYunDriveUser;
import top.xmlsj.signin.aliyundrive.service.AliyundriveUserService;
import top.xmlsj.signin.aliyundrive.task.AliYunDriveTask;
import top.xmlsj.signin.util.CoreUtil;
import top.xmlsj.signin.util.ExceptionConstants;

import javax.annotation.Resource;
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
public class AliYunDriveCheckTask implements AliYunDriveTask {

    private static final String NAME = "登录检查";

    @Resource
    private AliyundriveUserService userService;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        // 清空数据库表数据
        userService.truncateTable();
        AliYunDriveConfig config = CoreUtil.readAliYunDriveConfig();
        List<AliYunDriveUser> accounts = config.getAccounts();
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        //入库
        userService.saveBatch(accounts);
        accounts.forEach(u -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("refresh_token", u.getRefreshToken());
            String post = HttpRequest.post("https://auth.aliyundrive.com/v2/account/token").body(JSON.toJSONString(params))
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

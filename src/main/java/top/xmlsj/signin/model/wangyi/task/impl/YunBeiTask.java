package top.xmlsj.signin.model.wangyi.task.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.model.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.model.wangyi.domain.pojo.CloudBeiRes;
import top.xmlsj.signin.model.wangyi.service.MusicUserService;
import top.xmlsj.signin.model.wangyi.task.Task;
import top.xmlsj.signin.model.wangyi.util.NetEasseColudApi;
import top.xmlsj.signin.util.ExceptionConstants;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2023/3/16.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class YunBeiTask implements Task {
    private static final String NAME = "云贝";

    @Resource
    private MusicUserService userService;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        List<MusicUser> accounts = userService.list(new QueryWrapper<MusicUser>().eq("is_authenticated", 1));
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        accounts.forEach(u -> {
            log.info("用户：{} 开始云贝签到！！！", u.getName());
            String url = "https://music.163.com/weapi/point/dailyTask?csrf_token=" + u.getToken();
            Map<String, String> headers = new HashMap<>(3);
            headers.put("crypto", "weapi");
            headers.put("Cookie", u.getCookie());
            headers.put("token", u.getToken());
            JSONObject param = new JSONObject();
            param.put("type", 0);
            param.put("withCredentials", true);
            CloudBeiRes cloudBeiRes = NetEasseColudApi.api(param.toJSONString(), url, headers).toJavaObject(CloudBeiRes.class);
            if (cloudBeiRes.isSucceed()) {
                log.info("用户名：{}    获得{}个云贝", u.getName(), cloudBeiRes.getPoint());
            } else {
                log.error("用户名：{}签到失败！", u.getName());
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

package top.xmlsj.signin.script.wangyi.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.core.util.ExceptionConstants;
import top.xmlsj.signin.model.message.service.SendService;
import top.xmlsj.signin.script.wangyi.api.MusicApi;
import top.xmlsj.signin.script.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.script.wangyi.service.MusicUserService;
import top.xmlsj.signin.task.SigninTask;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created on 2023/3/21.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class WangYiLoginCheckTask implements SigninTask {

    private final String taskName = "登录检查";

    @Resource
    private MusicApi mapi;
    @Resource
    private MusicUserService userService;

    @Resource
    private SendService sendService;
    private int STATE = 0;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        List<MusicUser> accounts = userService.list();
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        accounts.forEach(u -> {
            log.debug("user：{}", u.toString());
            JSONObject jsonObject = mapi.userLevel(u.getCookie());
            log.debug(jsonObject.toString());
            Integer code = jsonObject.getInteger("code");
            if (code == 200) {
                u.setLevel(jsonObject.getJSONObject("data").getInteger("level"));
                u.setIsAuthenticated(1);
                userService.updateById(u);
            } else {
                log.info("用户 {} 登录验证错误请检查cookie！！！", u.getName());
                sendService.send("网易云音乐 : 用户 " + u.getName() + " 登录验证错误请检查cookie！！！");
            }
        });
        long auths = userService.count(new QueryWrapper<MusicUser>().eq("is_authenticated", 1));
        if (auths > 0) {
            // 如果无通过登录检测用户 状态码为0 后面项目不执行
            this.STATE = 1;
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

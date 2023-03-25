package top.xmlsj.signin.wangyi.task.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.util.ExceptionConstants;
import top.xmlsj.signin.wangyi.api.MusicApiService;
import top.xmlsj.signin.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.wangyi.domain.entity.WangYiConfig;
import top.xmlsj.signin.wangyi.service.MusicUserService;
import top.xmlsj.signin.wangyi.service.WangYiCoreService;
import top.xmlsj.signin.wangyi.task.Task;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created on 2023/3/21.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class WangYiLoginCheckTask implements Task {

    private final String taskName = "登录检查";
    @Resource
    private WangYiCoreService wangYiCoreService;
    @Resource
    private MusicApiService mapi;
    @Resource
    private MusicUserService userService;
    private int STATE = 0;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        // 清空数据库表数据
        userService.truncateTable();
        WangYiConfig config = wangYiCoreService.readWangYiConfig();
        List<MusicUser> accounts = config.getAccounts();
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        //入库
        userService.saveBatch(accounts);
        accounts.forEach(u -> {
            JSONObject jsonObject = mapi.userLevel(u.getToken(), u.getCookie());
            Integer code = jsonObject.getInteger("code");
            if (code == 200) {
                u.setLevel(jsonObject.getJSONObject("data").getInteger("level"));
                u.setIsAuthenticated(1);
                System.out.println(u);
                userService.updateById(u);
            } else {
                log.info("用户 {} 登录验证错误请检查cookie！！！" + u.getName());
            }
        });
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

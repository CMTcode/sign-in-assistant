package top.xmlsj.signin.model.baidu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.core.util.ExceptionConstants;
import top.xmlsj.signin.model.baidu.domain.entity.BaiduUser;
import top.xmlsj.signin.model.baidu.service.BaiduUserService;
import top.xmlsj.signin.model.baidu.util.TiebaSignUtil;
import top.xmlsj.signin.model.message.service.SendService;
import top.xmlsj.signin.task.SigninTask;

import java.util.List;

/**
 * Created on 2023/4/8.
 * 登录校验
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BaiduLoginVerifyTask implements SigninTask {
    private final SendService sendService;
    private final BaiduUserService userService;

    private int status = 0;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        List<BaiduUser> accounts = userService.list();
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        accounts.forEach(u -> {
            String tbstr = TiebaSignUtil.verification(u.getBduss());
            if (StringUtils.isBlank(tbstr)) {
                log.info("用户 {} 登录检查失败！请检查bduss", u.getName());
                sendService.send("用户" + u.getName() + "登录检查失败！请检查bduss");
            } else {
                u.setIsAuthenticated(1);
                userService.updateById(u);
            }
        });
        long auths = userService.count(new QueryWrapper<BaiduUser>().eq("is_authenticated", 1));
        if (auths > 0) {
            // 如果无通过登录检测用户 状态码为0 后面项目不执行
            this.status = 1;
        }

    }

    /**
     * 任务名
     *
     * @return taskName
     */
    @Override
    public String getName() {
        return "登录检查";
    }

    /**
     * 获取状态
     *
     * @return
     */
    @Override
    public int getState() {
        return status;
    }
}

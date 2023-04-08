package top.xmlsj.signin.model.wapj.task;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.wapj.constant.WAPJConst;
import top.xmlsj.signin.task.SigninTask;

/**
 * Created on 2023/4/6.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class WAPJSignInTask implements SigninTask {

    private final String cookie = "";

    /**
     * 任务实现
     */
    @Override
    public void run() {
        HttpResponse response = HttpRequest.get(WAPJConst.SIGIN_URL)
                .header(Header.HOST, WAPJConst.REFERER)
                .header(Header.USER_AGENT, WAPJConst.USER_AGENT)
                .cookie(cookie).execute();
        System.out.println(response.body());
    }

    /**
     * 任务名
     *
     * @return taskName
     */
    @Override
    public String getName() {
        return null;
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

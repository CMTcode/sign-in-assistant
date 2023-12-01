package top.xmlsj.signin.script.wangyi.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import top.xmlsj.signin.core.util.ExceptionConstants;
import top.xmlsj.signin.script.wangyi.api.MusicApi;
import top.xmlsj.signin.script.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.script.wangyi.service.MusicUserService;
import top.xmlsj.signin.task.SigninTask;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ForkManTou
 * @version 1.0
 * @time 2023/4/20.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Signin implements SigninTask {

    private final MusicApi api;
    private final MusicUserService userService;

    @Override
    public String getName() {
        return "每日签到";
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public void run() {
        List<MusicUser> accounts = userService.list(new QueryWrapper<MusicUser>().eq("is_authenticated", 1));
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_AVAILABLE);
        for (MusicUser account : accounts) {
            JSONObject result = api.dailySignin(account.getCookie(), 0);
            if (result.getInteger("code") == 200) {
                log.info("签到获得{}点经验", result.getInteger("point"));
            } else {
                log.warn("{}", result.getString("msg"));
            }
            // TODO 测试过只需要签到一个端就可以 总共5点经验
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(2);
            executor.setQueueCapacity(2);
            executor.setThreadNamePrefix("网易云音乐: 签到项目" + " -");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            CompletableFuture<JSONObject> web = CompletableFuture.supplyAsync(() -> api.dailySignin(account.getCookie(), 0), executor);
            CompletableFuture<JSONObject> az = CompletableFuture.supplyAsync(() -> api.dailySignin(account.getCookie(), 1), executor);
            try {
                JSONObject webr = web.get();
                JSONObject azr = az.get();
                if (webr.getInteger("code") == 200) {
                    log.info("安卓端 : 签到获得{}点经验", webr.getInteger("point"));
                } else {
                    log.warn("安卓端 : {}", webr.getString("msg"));
                }
                if (azr.getInteger("code") == 200) {
                    log.info("PC端 : 签到获得{}点经验", azr.getInteger("point"));
                } else {
                    log.warn("PC端 : {}", azr.getString("msg"));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                // 关闭线程池
                executor.shutdown();
            }
        }
    }
}

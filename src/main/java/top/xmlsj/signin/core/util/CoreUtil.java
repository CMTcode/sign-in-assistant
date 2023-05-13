package top.xmlsj.signin.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import top.xmlsj.signin.script.aliyundrive.service.AliyundriveUserService;
import top.xmlsj.signin.script.baidu.service.BaiduUserService;
import top.xmlsj.signin.script.wangyi.service.MusicUserService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created on 2023/3/19.
 * 核心工具类
 *
 * @author Yang YaoWei
 */
@Slf4j
public class CoreUtil {
    public static void printTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        log.info(time);
    }

    public static void taskSuspend() throws InterruptedException {
        Random random = new Random();
        int sleepTime = (int) ((random.nextDouble() + 0.5) * 3000);
        log.info("-----随机暂停{}ms-----\n", sleepTime);
        Thread.sleep(sleepTime);
    }


    /**
     * app 初始化
     *
     * @param context SpringApplicationContext
     */
    public static void initialize(ApplicationContext context) {
        AliyundriveUserService aliyundriveUserService = context.getBean(AliyundriveUserService.class);
        MusicUserService musicUserService = context.getBean(MusicUserService.class);
        BaiduUserService baiduUserService = context.getBean(BaiduUserService.class);
        // 异步去执行
        // 阿里云网盘
        CompletableFuture<Void> aliyundriveFuture = CompletableFuture.runAsync(aliyundriveUserService::init);
        // 网易云音乐
        CompletableFuture<Void> musicFuture = CompletableFuture.runAsync(musicUserService::init);
        // 百度
        CompletableFuture<Void> baiduFuture = CompletableFuture.runAsync(baiduUserService::init);
        // TODO 哔哩哔哩暂不通过这处理
        try {
            aliyundriveFuture.get();
            musicFuture.get();
            baiduFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


}

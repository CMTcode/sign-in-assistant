package top.xmlsj.signin.util;

import lombok.extern.slf4j.Slf4j;
import top.xmlsj.signin.core.config.app.AppConfig;
import top.xmlsj.signin.core.constant.ConfigPathConstants;
import top.xmlsj.signin.model.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.model.baidu.domain.pojo.BaiduConfig;
import top.xmlsj.signin.model.wangyi.domain.pojo.WangYiConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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
     * 读取网易云音乐配置文件
     *
     * @return 网易云音乐配置
     */
    public static WangYiConfig readWangYiConfig() {
        return YmlUtil.readConfig(ConfigPathConstants.WANGYI_CONFIG_PATH, WangYiConfig.class);
    }

    /**
     * 读取阿里云网盘配置文件
     *
     * @return
     */
    public static AliYunDriveConfig readAliYunDriveConfig() {
        return YmlUtil.readConfig(ConfigPathConstants.ALIYUNDRIVE_CONFIG_PATH, AliYunDriveConfig.class);
    }

    /**
     * 读取百度贴吧配置文件
     *
     * @return
     */
    public static BaiduConfig readBaiduConfig() {
        return YmlUtil.readConfig(ConfigPathConstants.BAIDU_CONFIG_PATH, BaiduConfig.class);
    }

    public static AppConfig readAppConfig() {
        return YmlUtil.readConfig(ConfigPathConstants.APP_CONFIG_PATH, AppConfig.class);
    }
}

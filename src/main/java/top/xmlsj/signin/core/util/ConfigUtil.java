package top.xmlsj.signin.core.util;

import top.xmlsj.signin.core.config.app.AppConfig;
import top.xmlsj.signin.core.enums.ConfigPathEnum;
import top.xmlsj.signin.model.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.model.baidu.domain.pojo.BaiduConfig;
import top.xmlsj.signin.model.bilbil.domain.entity.BilBilConfig;
import top.xmlsj.signin.model.wangyi.domain.pojo.WangYiConfig;

import java.io.IOException;

/**
 * @author ForkManTou
 * @version 1.0
 * @time 2023/4/20.
 */
public class ConfigUtil {
    private static <T> T readConfig(ConfigPathEnum path, Class<T> type) {
        try {
            return YmlUtil.readConfig(path.getPath(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取网易云音乐配置文件
     *
     * @return 网易云音乐配置
     */
    public static WangYiConfig readWangYiConfig() {
        return readConfig(ConfigPathEnum.WANGYI, WangYiConfig.class);
    }

    /**
     * 读取哔哩哔哩config
     *
     * @return
     */
    public static BilBilConfig readBilBilConfig() {
        return readConfig(ConfigPathEnum.BILBIL, BilBilConfig.class);
    }

    /**
     * 读取阿里云网盘配置文件
     *
     * @return
     */
    public static AliYunDriveConfig readAliYunDriveConfig() {
        return readConfig(ConfigPathEnum.ALIYUNDRIVE, AliYunDriveConfig.class);
    }

    /**
     * 读取百度贴吧配置文件
     *
     * @return
     */
    public static BaiduConfig readBaiduConfig() {
        return readConfig(ConfigPathEnum.BAIDU, BaiduConfig.class);
    }

    public static AppConfig readAppConfig() {
        return readConfig(ConfigPathEnum.APP, AppConfig.class);
    }
}

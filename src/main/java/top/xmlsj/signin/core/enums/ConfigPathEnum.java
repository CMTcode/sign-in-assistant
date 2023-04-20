package top.xmlsj.signin.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @time 2023-4-20
 */
@Getter
@AllArgsConstructor
public enum ConfigPathEnum {
    /**
     * 本脚本配置文件
     */
    APP("./config/appconfig.yml"),
    /**
     * 百度
     */
    BAIDU("./config/baidu.yml"),
    /**
     * 哔哩哔哩
     */
    BILBIL("./config/bilbil.yml"),
    /**
     * 网易云音乐
     */
    WANGYI("./config/wangyi.yml"),
    /**
     * 阿里云网盘
     */
    ALIYUNDRIVE("./config/aliyundrive.yml");

    private final String path;
}

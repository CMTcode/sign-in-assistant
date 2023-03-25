package top.xmlsj.signin.aliyundrive.util;

import top.xmlsj.signin.aliyundrive.domain.entity.AliYunDriveConfig;
import top.xmlsj.signin.config.ConfigPath;
import top.xmlsj.signin.util.YmlUtil;


/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
public class AliYunUtil {
    /**
     * 读取配置文件
     *
     * @return
     */
    public static AliYunDriveConfig readAliYunDriveConfigConfig() {
        return YmlUtil.readConfig(ConfigPath.ALIYUNDRIVE_CONFIG_PATH, AliYunDriveConfig.class);
    }
}

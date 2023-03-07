package top.xmlsj.sigin.util;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created on 2023/3/7.
 *
 * @author Yang YaoWei
 */
@Slf4j
public class YmlUtil {

    /**
     * 读取Yml配置文件
     * @param path
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T readConfigYml(String path, Class<T> type) {
        Yaml yaml = new Yaml();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.info("未找到 {} 配置文件", Arrays.stream(path.split("/")).reduce((f, s) -> s).orElse("Error"), e);
        }
        return  yaml.loadAs(inputStream, type);
    }
}

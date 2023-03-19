package top.xmlsj.signin.util;

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
     *
     * @param path yml文件路径
     * @param type 返回类型
     * @return 返回指定对象
     */
    public static <T> T readConfig(String path, Class<T> type) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.info("未找到 {} 配置文件", Arrays.stream(path.split("/")).reduce((f, s) -> s).orElse("Error"), e);
        }
        return new Yaml().loadAs(inputStream, type);
    }


    /**
     * 读取Yml配置文件
     *
     * @param path yml文件路径
     * @return 返回一个object对象
     */
    public static Object readConfig(String path) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.info("未找到 {} 配置文件", Arrays.stream(path.split("/")).reduce((f, s) -> s).orElse("Error"), e);
        }
        return new Yaml().load(inputStream);
    }
}

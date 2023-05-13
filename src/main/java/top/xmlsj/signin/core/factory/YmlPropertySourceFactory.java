package top.xmlsj.signin.core.factory;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

/**
 * Created on 2023/4/7.
 * Yaml 配置文件读取工厂类
 *
 * @author Yang YaoWei
 */
public class YmlPropertySourceFactory implements PropertySourceFactory {
    /**
     * Create a {@link PropertySource} that wraps the given resource.
     *
     * @param name     the name of the property source
     *                 (can be {@code null} in which case the factory implementation
     *                 will have to generate a name based on the given resource)
     * @param resource the resource (potentially encoded) to wrap
     * @return the new {@link PropertySource} (never {@code null})
     * @throws IOException if resource resolution failed
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        // 返回 yaml 属性资源
        return new YamlPropertySourceLoader()
                .load(resource.getResource().getFilename(), resource.getResource())
                .get(0);
    }
}

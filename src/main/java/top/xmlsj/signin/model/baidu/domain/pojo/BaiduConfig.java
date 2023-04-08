package top.xmlsj.signin.model.baidu.domain.pojo;

import lombok.Data;
import top.xmlsj.signin.model.baidu.domain.entity.BaiduUser;

import java.util.List;


/**
 * Created on 2023/3/5.
 * 百度配置类
 *
 * @author Yang YaoWei
 * @file config/baidu.yml
 */
@Data
public class BaiduConfig {
    private boolean enabled = false;
    private List<BaiduUser> accounts;
}

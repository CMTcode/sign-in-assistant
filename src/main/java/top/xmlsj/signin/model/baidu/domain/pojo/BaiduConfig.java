package top.xmlsj.signin.model.baidu.domain.pojo;

import lombok.Data;
import top.xmlsj.signin.model.baidu.domain.entity.Account;

import java.util.List;


/**
 * Created on 2023/3/5.
 *
 * @author Yang YaoWei
 */
@Data
public class BaiduConfig {
    private Boolean enabled;
    private List<Account> accounts;
}

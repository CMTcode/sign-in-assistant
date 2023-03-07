package top.xmlsj.sigin.baidu.domain.entity;

import lombok.Data;

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

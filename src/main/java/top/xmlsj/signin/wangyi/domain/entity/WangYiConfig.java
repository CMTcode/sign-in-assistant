package top.xmlsj.signin.wangyi.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * Created on 2023/3/18.
 *
 * @author Yang YaoWei
 */
@Data
public class WangYiConfig {

    private boolean enabled = false;
    private List<MusicUser> accounts;
}

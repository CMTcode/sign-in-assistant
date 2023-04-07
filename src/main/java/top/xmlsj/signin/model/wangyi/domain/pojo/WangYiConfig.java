package top.xmlsj.signin.model.wangyi.domain.pojo;

import lombok.Data;
import top.xmlsj.signin.model.wangyi.domain.entity.MusicUser;

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

package top.xmlsj.signin.model.message.domain.pojo;

import lombok.Data;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
@Data
public class MsgConfig {
    private String pushType = "none";
    private PushConfig pushConfig;
}

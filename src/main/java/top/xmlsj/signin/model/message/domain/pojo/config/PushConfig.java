package top.xmlsj.signin.model.message.domain.pojo.config;

import lombok.Data;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
@Data
public class PushConfig {
    private MailboxConfig mailbox;
    private ServerConfig server;
}

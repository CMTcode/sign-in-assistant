package top.xmlsj.signin.model.message.domain.pojo.config;

import lombok.Data;

/**
 * Created on 2023/4/1.
 * 消息配置
 *
 * @author Yang YaoWei
 */
@Data
public class MsgConfig {
    /**
     * 推送方式
     */
    private String pushType = "none";
    private PushConfig pushConfig;

    @Data
    public static class PushConfig {
        private MailboxConfig mailbox;
        private ServerConfig server;
    }
}

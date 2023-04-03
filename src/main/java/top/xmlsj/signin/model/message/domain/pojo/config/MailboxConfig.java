package top.xmlsj.signin.model.message.domain.pojo.config;

import lombok.Data;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
@Data
public class MailboxConfig {
    /**
     * 邮寄服务器地址
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;
    private boolean auth = true;
    private String form;
    /**
     * 发送邮寄账号
     */
    private String username;
    private String password;
    private String email;
}

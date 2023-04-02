package top.xmlsj.signin.model.message.service.st;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.model.message.domain.entity.MessageInfo;
import top.xmlsj.signin.model.message.domain.pojo.MailboxConfig;
import top.xmlsj.signin.model.message.service.MsgStrategyService;
import top.xmlsj.signin.util.CoreUtil;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
@Component("email")
public class MailboxMsgStrategyService implements MsgStrategyService {
    private static final MailboxConfig MAILBOX;

    static {
        MAILBOX = CoreUtil.readAppConfig().getMsg().getPushConfig().getMailbox();
    }

    private MailAccount initAccount() {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(MAILBOX.getHost());
        mailAccount.setPort(MAILBOX.getPort());
        mailAccount.setAuth(true);
        mailAccount.setUser(MAILBOX.getUsername());
        mailAccount.setPass(MAILBOX.getPassword());
        mailAccount.setFrom(MAILBOX.getForm());
        mailAccount.setStarttlsEnable(true);
        return mailAccount;
    }

    /**
     * 消息发送
     *
     * @param messageInfo
     * @return
     */
    @Override
    public JSONObject send(MessageInfo messageInfo) {
        return JSONObject.parseObject(MailUtil.send(initAccount(), MAILBOX.getEmail(), messageInfo.getTitle(), messageInfo.getMessage(), false));
    }
}

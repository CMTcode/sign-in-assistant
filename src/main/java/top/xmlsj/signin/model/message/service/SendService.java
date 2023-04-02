package top.xmlsj.signin.model.message.service;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.message.constant.StrategyConstants;
import top.xmlsj.signin.model.message.domain.entity.MessageInfo;
import top.xmlsj.signin.model.message.domain.pojo.MsgConfig;
import top.xmlsj.signin.util.CoreUtil;

import java.util.Arrays;


/**
 * Created on 2023/4/2.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class SendService {

    private static final MsgConfig PUSHCONFIG;

    static {
        PUSHCONFIG = CoreUtil.readAppConfig().getMsg();
    }

    private final MsgStrategyService msgStrategyService;
    private String title = "sigin签到助手";

    public SendService(MsgStrategyService msgStrategyService) {
        this.msgStrategyService = msgStrategyService;
    }

    public void send(String msg) {
        if (Arrays.asList(StrategyConstants.MSG_STRATEGY).contains(PUSHCONFIG.getPushType())) {
            if (!"none".equals(PUSHCONFIG.getPushType())) {
                MessageInfo info = MessageInfo.builder()
                        .title(title)
                        .messageType(PUSHCONFIG.getPushType())
                        .message(msg).build();
                JSONObject send = msgStrategyService.send(info);
                log.debug("{}", send);
            }
        } else {
            log.info("请配置正确的 pushType 推送方式值");
        }

    }
}

package top.xmlsj.signin.model.message.service;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.model.message.constant.StrategyConstants;
import top.xmlsj.signin.model.message.domain.pojo.MsgInfo;
import top.xmlsj.signin.model.message.domain.pojo.config.MsgConfig;

import java.util.Arrays;


/**
 * Created on 2023/4/2.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class SendService {

    private static final MsgConfig MSG_CONFIG;

    static {
        MSG_CONFIG = ConfigUtil.readAppConfig().getMsg();
    }


    private final MsgStrategyContext msgStrategyContext;

    public SendService(MsgStrategyContext msgStrategyContext) {
        this.msgStrategyContext = msgStrategyContext;
    }

    private String title = "sigin签到助手";


    public void send(String msg) {
        if (Arrays.asList(StrategyConstants.MSG_STRATEGY).contains(MSG_CONFIG.getPushType())) {
            if (!"none".equals(MSG_CONFIG.getPushType())) {
                MsgInfo info = MsgInfo.builder()
                        .title(title)
                        .messageType(MSG_CONFIG.getPushType())
                        .message(msg).build();
                JSONObject send = msgStrategyContext.send(info);
                log.debug("{}", send);
            }
        } else {
            log.info("请配置正确的 pushType 推送方式值");
        }

    }
}

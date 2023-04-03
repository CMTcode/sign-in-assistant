package top.xmlsj.signin.model.message.service;

import com.alibaba.fastjson2.JSONObject;
import top.xmlsj.signin.model.message.domain.pojo.MsgInfo;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
public interface MsgStrategyService {
    /**
     * 消息发送
     *
     * @param msgInfo
     * @return
     */
    JSONObject send(MsgInfo msgInfo);
}

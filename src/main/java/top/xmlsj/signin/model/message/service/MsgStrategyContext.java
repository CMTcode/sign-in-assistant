package top.xmlsj.signin.model.message.service;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.message.domain.entity.MessageInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
@Service
public class MsgStrategyContext {
    private final Map<String, MsgStrategyService> strategyMap = new ConcurrentHashMap<>();

    public MsgStrategyContext(Map<String, MsgStrategyService> strategyMap) {
        this.strategyMap.clear();
        this.strategyMap.putAll(strategyMap);
    }

    public JSONObject send(MessageInfo messageInfo) {
        return strategyMap.get(messageInfo.getMessageType()).send(messageInfo);
    }
}

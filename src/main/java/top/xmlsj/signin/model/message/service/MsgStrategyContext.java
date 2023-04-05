package top.xmlsj.signin.model.message.service;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.message.domain.pojo.MsgInfo;

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

    public MsgStrategyService getResources(MsgInfo msgInfo) {
        return strategyMap.get(msgInfo.getMessageType());
    }

    public JSONObject send(MsgInfo msgInfo) {
        return strategyMap.get(msgInfo.getMessageType()).send(msgInfo);
    }
}

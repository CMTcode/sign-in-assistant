package top.xmlsj.signin.model.message.service.st;

import com.alibaba.fastjson2.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.xmlsj.signin.core.util.ConfigUtil;
import top.xmlsj.signin.model.message.domain.pojo.MsgInfo;
import top.xmlsj.signin.model.message.service.MsgStrategyService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created on 2023/4/3.
 * 消息推送 server酱
 *
 * @author Yang YaoWei
 */
@Component("server")
@Slf4j
public class ServerMsg implements MsgStrategyService {

    private final static String KEY;

    static {
        KEY = ConfigUtil.readAppConfig().getMsg().getPushConfig().getServer().getKey();
    }

    /**
     * 消息发送
     *
     * @param msgInfo
     * @return
     */
    @Override
    public JSONObject send(MsgInfo msgInfo) {
        HashMap<String, Object> query = new HashMap<>();
        query.put("title", msgInfo.getTitle());
        query.put("desp", msgInfo.getMessage());
        HttpResponse<String> response = Unirest.post("https://sctapi.ftqq.com/" + KEY + ".send")
                .queryString(query)
                .contentType("application/json")
                .charset(StandardCharsets.UTF_8).asString();
        if (response.isSuccess()) {
            return JSONObject.parseObject(response.getBody());
        }
        log.error("server酱推送异常,请检查key");
        return null;
    }
}

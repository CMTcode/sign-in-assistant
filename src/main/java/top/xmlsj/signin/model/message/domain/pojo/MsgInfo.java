package top.xmlsj.signin.model.message.domain.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * Created on 2023/4/1.
 *
 * @author Yang YaoWei
 */
@Data
@Builder
public class MsgInfo {

    /**
     * 发送消息服务类型
     */
    private String messageType;
    private String title;
    /**
     * 消息体
     */
    private String message;
}

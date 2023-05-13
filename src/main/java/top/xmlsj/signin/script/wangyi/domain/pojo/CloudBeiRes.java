package top.xmlsj.signin.script.wangyi.domain.pojo;

import lombok.Data;

/**
 * Created on 2022/11/5.
 * 云贝签到返回信息
 *
 * @author Yang YaoWei
 */
@Data
public class CloudBeiRes {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 云贝点
     */
    private Long point;

    public boolean isSucceed() {
        return this.code.equals(200);
    }
}

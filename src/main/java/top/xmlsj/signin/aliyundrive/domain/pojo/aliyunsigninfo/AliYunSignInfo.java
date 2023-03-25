package top.xmlsj.signin.aliyundrive.domain.pojo.aliyunsigninfo;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@lombok.Data
public class AliYunSignInfo {
    private boolean success;
    private Object code;
    private Object message;
    private Object totalCount;
    private Object nextToken;
    private Object maxResults;
    private Result result;
    private Object arguments;
}

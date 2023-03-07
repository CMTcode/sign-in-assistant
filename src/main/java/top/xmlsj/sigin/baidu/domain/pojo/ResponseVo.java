package top.xmlsj.sigin.baidu.domain.pojo;

import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Author: Yang
 * Date: 2019/7/10
 * Time: 18:11
 * Description: 响应实体类封装
 */
public class ResponseVo {
    //    响应字符串
    private String responseStr;
    //    响应response对象
    private CloseableHttpResponse response;

    public ResponseVo(String responseStr, CloseableHttpResponse response) {
        this.responseStr = responseStr;
        this.response = response;
    }

    public ResponseVo() {
    }

    public String getResponseStr() {
        return responseStr;
    }

    public ResponseVo setResponseStr(String responseStr) {
        this.responseStr = responseStr;
        return this;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public ResponseVo setResponse(CloseableHttpResponse response) {
        this.response = response;
        return this;
    }
}

package top.xmlsj.signin.baidu.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import top.xmlsj.signin.baidu.domain.pojo.ResponseVo;

import java.io.IOException;
import java.util.*;

/**
 * Author: Yang
 * Date: 2019/7/10
 * Time: 18:10
 * Description: POST数据工具类
 */
public class HttpClientUtil {
    private static final PoolingHttpClientConnectionManager cm;

    static {
        cm = new PoolingHttpClientConnectionManager();
        //    设置最大连接数
        cm.setMaxTotal(200);
        //    设置每个主机的并发数
        cm.setDefaultMaxPerRoute(20);
    }

    /**
     * post封装
     *
     * @param url    请求URL
     * @param header 请求头
     * @param body   请求体
     * @return 返回Response与页面字符
     * @throws Exception
     */
    public static ResponseVo postClient(String url, String header, String body) throws Exception {
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();
        HttpPost httpPost = new HttpPost(url);
        if (StringUtils.isNotBlank(header)) {
            for (Map.Entry<String, String> entry : covertParam(header)) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        List<NameValuePair> pairs = new ArrayList<>();
        if (StringUtils.isNotBlank(body)) {
            for (Map.Entry<String, String> entry : covertParam(body)) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs, "utf-8");
        httpPost.setEntity(formEntity);
        String content = null;
        CloseableHttpResponse response = null;
        ResponseVo responseVo = null;
        try {
            //使用HttpClient发起请求
            response = client.execute(httpPost);
            //判断响应状态码是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                //如果为200表示请求成功，获取返回数据
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
                responseVo = new ResponseVo().setResponse(response).setResponseStr(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放连接
            if (response == null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseVo;
    }

    /**
     * 校验、转换参数
     */
    public static Set<Map.Entry<String, String>> covertParam(String params) throws Exception {
        String[] paramses;
        if (params.contains("=") && params.contains("&")) {
            paramses = params.split("&");
        } else if (params.contains("=") && !params.contains("&")) {
            paramses = new String[1];
            paramses[0] = params;
        } else {
            throw new Exception("参数请确保都为键值对形式");
        }
        Map<String, String> map = new HashMap<>();
        for (String s : paramses) {
            String[] ss = s.split("=");
            map.put(ss[0], s.substring(ss[0].length() + 1));
        }
        return map.entrySet();
    }
}

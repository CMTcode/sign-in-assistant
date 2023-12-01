package top.xmlsj.signin.script.wangyi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kong.unirest.core.CookieSpecs;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Map;

public class NetEasseColudApi {

    /**
     * 封装的网易云接口
     *
     * @param param  参数
     * @param url    请求地址
     * @param header 请求头
     * @return
     */
    public static JSONObject api(String param, String url, Map<String, String> header) {
        JSONObject result = new JSONObject();
        boolean hasParam = StringUtils.isNotBlank(param);
        ArrayList<BasicNameValuePair> list = new ArrayList<>();
        if (hasParam) {
            Map<String, String> forms = EncryptUtil.encrypt(param);
            forms.forEach((key, value) -> list.add(new BasicNameValuePair(key, value)));
        }
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建cookie store的本地实例
        CookieStore cookieStore = new BasicCookieStore();
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
//        HttpHost proxy = new HttpHost("222.64.95.60", 9000);
        // 创建一个HttpClient
        CloseableHttpClient hc = HttpClients.custom().setDefaultRequestConfig(globalConfig)
//                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
//        httpPost.setConfig(globalConfig);
        header.forEach(httpPost::addHeader);
        httpPost.addHeader("Referer", "https://music.163.com/");
        httpPost.addHeader("Origin", "http://music.163.com");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        try {
            if (hasParam) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = hc.execute(httpPost);
//            HttpResponse response = hc.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String str = EntityUtils.toString(httpEntity);
            result = JSON.parseObject(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

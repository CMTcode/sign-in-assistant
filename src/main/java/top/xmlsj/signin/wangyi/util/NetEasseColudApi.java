package top.xmlsj.signin.wangyi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.*;
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
            HttpResponse response = hc.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String str = EntityUtils.toString(httpEntity);
            result = JSON.parseObject(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 可以获取响应信息的接口
     *
     * @param param  参数
     * @param url    请求地址
     * @param header 请求头
     * @return
     */
    public static HttpResponse apiResponse(String param, String url, Map<String, String> header) {
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建cookie store的本地实例
        CookieStore cookieStore = new BasicCookieStore();
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        // 创建一个HttpClient
        CloseableHttpClient hc = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setDefaultCookieStore(cookieStore).build();
        JSONObject result = new JSONObject();
        boolean hasParam = StringUtils.isNotBlank(param);
        ArrayList<BasicNameValuePair> list = new ArrayList<>();
        if (hasParam) {
            Map<String, String> forms = EncryptUtil.encrypt(param);
            forms.forEach((key, value) -> list.add(new BasicNameValuePair(key, value)));
        }
        HttpPost httpPost = new HttpPost(url);
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
            HttpResponse response = hc.execute(httpPost);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject apiWithProxy(String param, String url, Map<String, String> header, String host, int port) {
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
        HttpHost proxy = new HttpHost(host, port);
        // 创建一个HttpClient
        CloseableHttpClient hc = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(globalConfig);
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
            HttpResponse response = hc.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String str = EntityUtils.toString(httpEntity);
            result = JSON.parseObject(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isValid(String ip, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        try {
            URLConnection httpCon = new URL("https://music.163.com/").openConnection(proxy);
            httpCon.setConnectTimeout(10000);
            httpCon.setReadTimeout(10000);
            int code = ((HttpURLConnection) httpCon).getResponseCode();
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

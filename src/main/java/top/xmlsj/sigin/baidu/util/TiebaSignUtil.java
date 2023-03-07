package top.xmlsj.sigin.baidu.util;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * Author: Yang
 * Date: 2019/7/19
 * Time: 0:06
 * Description: 签到请求参数等内容封装
 */
public class TiebaSignUtil {


    /**
     * 验证bduss 通过返回tbs
     *
     * @param bduss
     * @return
     * @throws Exception
     */
    public static String verification(String bduss) {
        JSONObject tbs = null;
        try {
            tbs = (JSONObject) JSON.parse(getTbs(bduss));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!"1".equals(tbs.getString("is_login"))) {
            return null;
        }
        return tbs.getString("tbs");
    }

    /**
     * 获取贴吧状态码 tbs
     */
    public static String getTbs(String bduss) throws Exception {
        String header = "Cookie=" + "BDUSS=" + bduss;
        return HttpClientUtil.postClient("http://tieba.baidu.com/dc/common/tbs", header, "").getResponseStr();
    }

    /**
     * 获取所有关注贴吧
     */
    public static String getForums(String bduss) throws Exception {
        String header = "Cookie=BDUSS=" + bduss + "&" +
                "Content-Type=application/x-www-form-urlencoded&Charset=UTF-8&net=3&" +
                "User-Agent=bdtb for Android 8.4.0.1&Connection=Keep-Alive&Accept-Encoding=gzip&" +
                "Host=c.tieba.baidu.com";
        String md5Hex = DigestUtils.md5Hex("BDUSS=" + bduss +
                "_client_version=8.1.0.4page_no=1page_size=400tiebaclient!!!");
        String body = "BDUSS=" + bduss + "&" +
                "_client_version=8.1.0.4&page_no=1&page_size=400&sign=" +
                md5Hex;
        return HttpClientUtil.postClient("http://c.tieba.baidu.com/c/f/forum/like", header, body).getResponseStr();
    }

    /**
     * 分页获取所有关注贴吧
     */
    public static String getForumsPage(int pageNo, int pageSize, String bduss) throws Exception {
        String header = "Cookie=BDUSS=" + bduss + "&" +
                "Content-Type=application/x-www-form-urlencoded&Charset=UTF-8&net=3&" +
                "User-Agent=bdtb for Android 8.4.0.1&Connection=Keep-Alive&Accept-Encoding=gzip&" +
                "Host=c.tieba.baidu.com";
        String pageString = "BDUSS=" + bduss + "&" +
                "_client_version=8.1.0.4&page_no=" + pageNo + "&page_size=" +
                pageSize;
        String md5Hex = DigestUtils.md5Hex(pageString.replace("&", "") +
                "tiebaclient!!!");
        String body = pageString +
                "&sign=" +
                md5Hex;
        return HttpClientUtil.postClient("http://c.tieba.baidu.com/c/f/forum/like", header, body).getResponseStr();
    }

    /**
     * 签到
     */
    public static String signForums(String name, String id, String tbs, String bduss) throws Exception {
        String header = "Cookie=BDUSS=" + bduss + "&" +
                "Content-Type=application/x-www-form-urlencoded&Charset=UTF-8&net=3&" +
                "User-Agent=bdtb for Android 8.4.0.1&Connection=Keep-Alive&Accept-Encoding=gzip&" +
                "Host=c.tieba.baidu.com";
        String md5Hex = DigestUtils.md5Hex("BDUSS=" + bduss +
                "fid=" + id + "kw=" + name + "tbs=" + tbs + "tiebaclient!!!");
        String body = "BDUSS=" + bduss +
                "&fid=" + id + "&kw=" + name +
                "&sign=" + md5Hex + "&tbs=" + tbs;
        return HttpClientUtil.postClient("http://c.tieba.baidu.com/c/c/forum/sign", header, body).getResponseStr();
    }

    /**
     * VIP签到
     */
    public static String vipSign(String tbs, String bduss) throws Exception {
        String firefoxHeader =
                "Content-Type: application/x-www-form-urlencoded\n" +
                        "Charset: UTF-8\n" +
                        "net: 3\n" +
                        "User-Agent: bdtb for Android 8.4.0.1\n" +
                        "Connection: Keep-Alive\n" +
                        "Accept-Encoding: gzip\n" +
                        "Host: c.tieba.baidu.com";
        firefoxHeader = firefoxHeader.replaceAll(": ", "=").replaceAll("\n", "&");
        String header = "Cookie=BDUSS=" + bduss + "&" +
                firefoxHeader;
        String body = "ie=utf-8&tbs=" + tbs;
        return HttpClientUtil.postClient("http://tieba.baidu.com/tbmall/onekeySignin1", header, body).getResponseStr();
    }

    /**
     * 获取贴吧总数
     *
     * @param forumsPage
     * @return
     */
    public static Integer getTotalTb(String forumsPage) {
        JSONObject result = (JSONObject) JSONObject.parse(forumsPage);
        JSONArray forum_list_non = result.getJSONObject("forum_list").getJSONArray("non-gconforum");
        JSONArray forum_list = result.getJSONObject("forum_list").getJSONArray("gconforum");
        return (forum_list_non == null ? 0 : forum_list_non.size()) + (forum_list == null ? 0 : forum_list.size());
    }


}

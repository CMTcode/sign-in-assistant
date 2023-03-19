package top.xmlsj.signin.wangyi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.xmlsj.signin.wangyi.util.NetEasseColudApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2022/12/16.
 *
 * @author Yang YaoWei
 */
@Controller
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicApiController {


    /**
     * 二维码登录前获取唯一key
     *
     * @return
     */
    @RequestMapping("/getKey")
    @ResponseBody
    public JSONObject getKey() {
        String url = "https://music.163.com/weapi/login/qrcode/unikey";
        return NetEasseColudApi.api("{\"type\":1}", url, new HashMap<>());
    }


    /**
     * 检查扫码状态
     *
     * @param code
     * @param response
     * @return
     */
    @RequestMapping("/checkCodeStatus")
    @ResponseBody
    public JSONObject checkStatus(String code, HttpServletResponse response) {
        JSONObject result;
        String url = "https://music.163.com/weapi/login/qrcode/client/login";
        JSONObject param = new JSONObject();
        param.put("type", 1);
        param.put("key", code);
        HttpResponse res = NetEasseColudApi.apiResponse(param.toJSONString(), url, new HashMap<>());
        try {
            assert res != null;
            HttpEntity httpEntity = res.getEntity();
            String str = EntityUtils.toString(httpEntity);
            result = JSON.parseObject(str);
            System.out.println(result);
            if (result.getInteger("code") == 803) {
                //登录授权成功
                Header[] allHeader = res.getHeaders("set-cookie");
                if (allHeader == null || allHeader.length == 0) {
                    allHeader = res.getHeaders("Set-Cookie");
                }
                StringBuilder cookie = new StringBuilder();
                for (Header header : allHeader) {
                    cookie.setLength(0);
                    String value = header.getValue();
                    String[] valueArr = value.split(";");
                    for (String s : valueArr) {
                        if (s.startsWith("Domain") || s.startsWith(" Domain")) {

                        } else {
                            cookie.append(s);
                            cookie.append(";");
                            if (s.startsWith("__csrf")) {
                                response.setHeader("tokenen", s.substring(s.indexOf("=") + 1));
                            }
                        }
                        value = cookie.toString();
                    }
                    cookie.deleteCharAt(cookie.length() - 1);
                    cookie.deleteCharAt(cookie.length() - 1);
                    System.out.println(header.getName() + ":" + cookie);
                    response.addHeader(header.getName(), value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }


    /**
     * 单纯的查看登录信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/user/detail")
    @ResponseBody
    public JSONObject getLoginInfo(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String url = "https://music.163.com/weapi/w/nuser/account/get?csrf_token=" + request.getHeader("tokenen");
        Map<String, String> headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", request.getHeader("Cookie"));
        headers.put("token", request.getHeader("tokenen"));
        JSONObject param = new JSONObject();
//            param.put("type", 1);
        param.put("withCredentials", true);
        JSONObject info = NetEasseColudApi.api(param.toJSONString(), url, headers);
        System.out.println("info:::" + info);
        result.put("result", info);
        return result;
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/user/account")
    @ResponseBody
    public JSONObject getUserAccount(HttpServletRequest request) {
        String url = "https://music.163.com/api/nuser/account/get?csrf_token=" + request.getHeader("tokenen");
        Map<String, String> headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", request.getHeader("Cookie"));
        headers.put("token", request.getHeader("tokenen"));
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        return NetEasseColudApi.api(param.toJSONString(), url, headers);
    }
}

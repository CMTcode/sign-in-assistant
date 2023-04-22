package top.xmlsj.signin.model.wangyi.api;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.wangyi.util.NetEasseColudApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2022/12/15.
 *
 * @author Yang YaoWei
 */
@Service
public class MusicApi {
    /**
     * 获取每日推荐歌单
     *
     * @param cookie
     * @return
     */
    public JSONObject getRecommentSongs(String cookie) {
        String url = "https://music.163.com/api/v1/discovery/recommend/resource?csrf_token=";
        Map headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", cookie);
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        JSONObject info = NetEasseColudApi.api(param.toJSONString(), url, headers);
        return info;
    }

    /**
     * 听歌
     *
     * @param cookie
     * @param songId     歌曲id
     * @param playListId 歌单id
     * @return
     */
    public JSONObject listenSong(String cookie, String songId, String playListId) {
        String url = "https://music.163.com/weapi/feedback/weblog?csrf_token=";
        Map headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", cookie);
        JSONObject param = new JSONObject();
        param.put("logs", "[{\"action\":\"play\",\"json\":{\"download\":0,\"end\":\"playend\",\"id\":" + songId + ",\"sourceId\":" + playListId + ",\"time\":" + "240" + ",\"type\":\"song\",\"wifi\":0}}]");
        param.put("withCredentials", true);
        //使用代理
//        if (StringUtils.isBlank(ip) || port == 0) {//当ip为空或者端口号为0时无法代理
//            JSONObject info = NetEasseColudApi.api(param.toJSONString(), url, headers);
//            return info;
//        } else {
//            JSONObject info = NetEasseColudApi.apiWithProxy(param.toJSONString(), url, headers, ip, port);
//            return info;
//        }
        //不使用代理
        JSONObject info = NetEasseColudApi.api(param.toJSONString(), url, headers);
        return info;
    }

    /**
     * 获取歌单详情
     *
     * @param sourceId
     * @param cookie
     * @return
     */
    public JSONObject resourceDetail(long sourceId, String cookie) {
        String url = "https://music.163.com/weapi/v6/playlist/detail?csrf_token=";
        Map headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", cookie);
        JSONObject param = new JSONObject();
        param.put("id", sourceId);
        param.put("n", 100000);
        param.put("s", 8);
        param.put("withCredentials", true);
        JSONObject info = NetEasseColudApi.api(param.toJSONString(), url, headers);
        return info;
    }


    /**
     * 获取用户
     *
     * @param cookie
     * @return
     */
    public JSONObject userLevel(String cookie) {
        String url = "https://music.163.com/weapi/user/level?csrf_token=";
        Map headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", cookie);
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        JSONObject info = NetEasseColudApi.api(param.toJSONString(), url, headers);
        return info;
    }

    /**
     * 签到
     * 0为安卓端签到 3点经验, 1为网页签到,2点经验
     *
     * @param cookie
     * @return
     */
    public JSONObject dailySignin(String cookie, int type) {
        String url = "https://music.163.com/weapi/point/dailyTask";
        Map headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", cookie);
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        param.put("type", StrUtil.nullToDefault(Integer.toString(type), "0"));
        return NetEasseColudApi.api(param.toJSONString(), url, headers);
    }

    /**
     * 解析歌单
     *
     * @param resourceJSON
     * @param cookie
     * @return
     */
    public Map<Long, List<Long>> parsePlayList(JSONObject resourceJSON, String cookie) {
        Map<Long, List<Long>> resultMap = new HashMap<>();
        if (resourceJSON != null && resourceJSON.getInteger("code") == 200) {
            JSONArray sourceArr = resourceJSON.getJSONArray("recommend");
            for (int i = 0; i < sourceArr.size(); i++) {
                JSONObject resource = sourceArr.getJSONObject(i);
                long sourceId = resource.getLong("id");
                JSONObject songListJSON = resourceDetail(sourceId, cookie);
                if (songListJSON != null && songListJSON.getInteger("code") == 200) {
                    JSONObject playlist = songListJSON.getJSONObject("playlist");
                    JSONArray trackIds = playlist.getJSONArray("trackIds");
                    List<Long> currentList = trackIds.stream()
                            .map(item -> JSONObject.parseObject(item.toString()).getLong("id"))
                            .collect(Collectors.toList());
                    resultMap.put(sourceId, currentList);
                }
            }
        }
        return resultMap;
    }
}

package top.xmlsj.signin.model.wangyi.api;

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
public class MusicApiService {

    private Map<String, String> initHeaders(String cookie) {
        Map<String, String> headers = new HashMap<>();
        headers.put("crypto", "weapi");
        headers.put("Cookie", cookie);
        return headers;
    }

    /**
     * 获取推荐歌单
     *
     * @param cookie
     * @return
     */
    public JSONObject getRecommendedPlay(String cookie) {
        String url = "https://music.163.com/api/v1/discovery/recommend/resource";
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        return NetEasseColudApi.api(param.toJSONString(), url, initHeaders(cookie));
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
        String url = "https://music.163.com/weapi/feedback/weblog";
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
        return NetEasseColudApi.api(param.toJSONString(), url, headers);
    }

    /**
     * 获取歌单详情
     *
     * @param sourceId
     * @param cookie
     * @return
     */
    public JSONObject resourceDetail(long sourceId, String cookie) {
        String url = "https://music.163.com/weapi/v6/playlist/detail";
        JSONObject param = new JSONObject();
        param.put("id", sourceId);
        param.put("n", 100000);
        param.put("s", 8);
        param.put("withCredentials", true);
        return NetEasseColudApi.api(param.toJSONString(), url, initHeaders(cookie));
    }


    /**
     * 获取用户等级信息
     *
     * @param cookie
     * @return
     */
    public JSONObject userLevel(String cookie) {
        String url = "https://music.163.com/weapi/user/level";
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        return NetEasseColudApi.api(param.toJSONString(), url, initHeaders(cookie));
    }

    public JSONObject vipTaskReward(String cookie) {
        String url = "https://music.163.com/api/vipnewcenter/app/level/task/reward/get";
        JSONObject param = new JSONObject();
        param.put("withCredentials", true);
        return NetEasseColudApi.api(param.toJSONString(), url, initHeaders(cookie));
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
                String copywriter = resource.getString("copywriter");
                long sourceId = resource.getLong("id");
                JSONObject songListJson = resourceDetail(sourceId, cookie);
                if (songListJson != null && songListJson.getInteger("code") == 200) {
                    JSONObject playlist = songListJson.getJSONObject("playlist");
                    JSONArray trackIds = playlist.getJSONArray("trackIds");
                    List<Long> currentList = trackIds.stream().map(item -> JSONObject.parseObject(item.toString()).getLong("id")).collect(Collectors.toList());
                    resultMap.put(sourceId, currentList);
                }
//
            }
        }
        return resultMap;
    }
}

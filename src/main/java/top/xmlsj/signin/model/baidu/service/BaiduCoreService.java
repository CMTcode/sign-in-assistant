package top.xmlsj.signin.model.baidu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.xmlsj.signin.model.baidu.domain.pojo.forums.Forum;
import top.xmlsj.signin.model.baidu.domain.pojo.forums.Rows;
import top.xmlsj.signin.model.baidu.util.TiebaSignUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created on 2023/4/8.
 *
 * @author Yang YaoWei
 */
@Service
@Slf4j
public class BaiduCoreService {

    /**
     * 签到
     *
     * @param name
     * @param forum
     * @param tbstr
     * @param bduss
     * @param reForum
     */
    public void signin(String name, Forum forum, String tbstr, String bduss, Set<Forum> reForum) {
        String signResult;
        try {
            signResult = TiebaSignUtil.signForums(forum.getName(), forum.getId(), tbstr, bduss);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject result = (JSONObject) JSON.parse(signResult);
        if (signResult.contains("error_code") && signResult.contains("error_msg")) {
            String errorMsg = result.getString("error_msg");
            log.warn(name + " : 签到{}吧 失败！！: {}", forum.getName(), errorMsg);
            if (!errorMsg.contains("已经签过")) {
                reForum.add(forum);
            }
        } else {
            JSONObject userInfo = result.getJSONObject("user_info");
            log.info(name + " : 签到{}吧成功!经验+{},今天第{}个签到", forum.getName(), userInfo.getString("sign_bonus_point"), userInfo.getString("user_sign_rank"));
        }
    }

    /**
     * 获取关注列表
     *
     * @param bduss
     * @return
     */
    public List<Forum> getForums(String bduss) {
        List<Forum> forums = new LinkedList<>();
        String forumsPage = null;
        try {
            forumsPage = TiebaSignUtil.getForums(bduss);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject result = (JSONObject) JSONObject.parse(forumsPage);
        Rows rows = result.toJavaObject(Rows.class);
        rows.getForumList().getGconforum().forEach(b -> {
            Forum forum = new Forum();
            BeanUtils.copyProperties(b, forum);
            forums.add(forum);
        });
        rows.getForumList().getNonGconforum().forEach(b -> {
            Forum forum = new Forum();
            BeanUtils.copyProperties(b, forum);
            forums.add(forum);
        });
        return forums;
    }
}

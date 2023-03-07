package top.xmlsj.sigin.baidu.domain.pojo.forums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created on 2022/11/11.
 *
 * @author Yang
 */
@Data
public class Rows {
    private int ctime;
    @JSONField(name = "error_code")
    private String errorCode;
    @JSONField(name = "forum_list")
    private ForumList forumList;
    @JSONField(name = "has_more")
    private String hasMore;
    private int logid;
    @JSONField(name = "server_time")
    private String serverTime;
    private int time;

    @Data
    public static class ForumList {
        private List<Gconforum> gconforum;
        @JSONField(name = "non-gconforum")
        private List<NonGconforum> nonGconforum;
    }

    @Data
    public static class Gconforum {
        private String avatar;
        @JSONField(name = "cur_score")
        private String curScore;
        @JSONField(name = "favo_type")
        private String favoType;
        private String id;
        @JSONField(name = "level_id")
        private String levelId;
        @JSONField(name = "level_name")
        private String levelName;
        @JSONField(name = "levelup_score")
        private String levelupScore;
        private String name;
        private String slogan;

    }

    @Data
    public static class NonGconforum {
        private String avatar;
        @JSONField(name = "cur_score")
        private String curScore;
        @JSONField(name = "favo_type")
        private String favoType;
        private String id;
        @JSONField(name = "level_id")
        private String levelId;
        @JSONField(name = "level_name")
        private String levelName;
        @JSONField(name = "levelup_score")
        private String levelupScore;
        private String name;
        private String slogan;

    }
}





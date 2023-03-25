package top.xmlsj.signin.aliyundrive.domain.pojo.aliyunsigninfo;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@lombok.Data
public class SignInLog {
    private long day;
    private Status status;
    private String icon;
    private Object notice;
    private Type type;
    private Themes themes;
    private String calendarChinese;
    private String calendarDay;
    private String calendarMonth;
    private Poster poster;
    private Reward reward;
    private boolean isReward;
}

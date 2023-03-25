package top.xmlsj.signin.aliyundrive.domain.pojo.aliyunsigninfo;

import java.util.List;

/**
 * Created on 2023/3/25.
 *
 * @author Yang YaoWei
 */
@lombok.Data
public class Result {
    private String subject;
    private String title;
    private String description;
    private boolean isReward;
    private String blessing;
    private long signInCount;
    private String signInCover;
    private String signInRemindCover;
    private String rewardCover;
    private List<SignInLog> signInLogs;
}

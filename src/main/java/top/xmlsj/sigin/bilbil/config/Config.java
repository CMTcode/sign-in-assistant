package top.xmlsj.sigin.bilbil.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * Auto-generated: 2020-10-13 17:10:40
 *
 * @author ForkManTou
 * @create 2020/10/13 17:11
 */
@Slf4j
@Data
public class Config {

    /**
     * 每日设定的投币数 [0,5]
     */
    public static Integer numberOfCoins = 5;
    /**
     * 投币时是否点赞 [0,1]
     */
    public static Integer selectLike = 1;
    /**
     * 年度大会员自动充电[false,true]
     */
    public static Boolean monthEndAutoCharge = true;
    /**
     * 自动打赏快过期礼物[false,true]
     */
    public static Boolean giveGift = true;
    /**
     * 打赏快过期礼物对象，为http://live.bilibili.com/后的数字
     * 填0表示随机打赏。
     */
    public static String upLive;
    /**
     * 执行客户端操作时的平台 [ios,android]
     */
    public static String devicePlatform = "ios";
    /**
     * 投币优先级 [0,1]
     * 0：优先给热榜视频投币，1：优先给关注的up投币
     */
    public static Integer coinAddPriority = 1;
    public static String userAgent;
    public static Boolean skipDailyTask = false;
    public static String chargeForLove;
    public static Integer reserveCoins;

    @Override
    public String toString() {
        return "配置信息{" +
                "每日投币数为：" + numberOfCoins +
                "分享时是否点赞：" + selectLike +
                "月底是否充电：" + monthEndAutoCharge +
                "执行app客户端操作的系统是：" + devicePlatform +
                "投币策略：" + coinAddPriority + "\n" +
                "UA是：" + userAgent + "\n" +
                "是否跳过每日任务：" + skipDailyTask +
                '}';
    }

}

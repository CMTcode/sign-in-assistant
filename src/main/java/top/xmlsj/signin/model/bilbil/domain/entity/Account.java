package top.xmlsj.signin.model.bilbil.domain.entity;

import lombok.Data;

/**
 * Created on 2023/3/5.
 *
 * @author Yang YaoWei
 */
@Data
public class Account {

    private String name;
    private String DEDEUSERID;
    private String SESSDATA;
    private String BILI_JCT;

    /**
     * 预留的硬币数，默认100
     */
    private int reserveCoins = 250;

    /**
     * 送礼 up 主的 uid，默认0为自己
     */
    private int upLive = 0;

    /**
     * 充电对象的 uid，默认0为自己
     */
    private int chargeForLove = 0;

    /**
     * 浏览器 UA
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.37";

    /**
     * 跳过签到，默认0不跳过，其他为跳过
     */
    private int skipDailyTask = 0;


}

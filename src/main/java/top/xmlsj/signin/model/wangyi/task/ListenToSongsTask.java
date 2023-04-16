package top.xmlsj.signin.model.wangyi.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.xmlsj.signin.model.wangyi.api.MusicApiService;
import top.xmlsj.signin.model.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.model.wangyi.service.MusicUserService;
import top.xmlsj.signin.task.SigninTask;
import top.xmlsj.signin.util.ExceptionConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2023/3/18.
 *
 * @author Yang YaoWei
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ListenToSongsTask implements SigninTask {

    private static final String NAME = "每日300首";

    private final MusicApiService mapi;
    @Resource
    private MusicUserService userService;

    /**
     * 任务实现
     */
    @Override
    public void run() {
        List<MusicUser> accounts = userService.list(new QueryWrapper<MusicUser>().eq("is_authenticated", 1));
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_AVAILABLE);
        accounts.forEach(this::listenSongsDaily);
    }

    /**
     * 任务名
     *
     * @return taskName
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 获取状态
     *
     * @return
     */
    @Override
    public int getState() {
        return 0;
    }


    public void listenSongsDaily(MusicUser user) {
        if (user.getLevel() < 10) {
            //因为每天上限是300首，但是要歌曲不重复，如果需要不重复的300首之后停止，请轮询用户等级信息接口获取听歌量后自行判断，successCount此处只能作为记录请求次数，无法确定是否成功刷歌
            int successCount = 0;
            int nowPlayCount = Integer.parseInt(mapi.userLevel(user.getCookie()).getJSONObject("data").getString("nowPlayCount"));
            int upperLimit = nowPlayCount + 300;
            log.info("每日听歌打卡=========================");
            //判断是否需要重试
            //首先获取可用的代理ip池
//        Map<String, Integer> ipPool = getIpPool();
//        Set<String> keyList = ipPool.keySet();//所有代理的ip集合,如果不使用代理，请注释掉一下带有proxy注释的代码
//
//        if (keyList.size() <= 1) {
//            throw new RuntimeException("请设置好代理后再使用~~~");
//        }
            //获取每日推荐歌单，保证是不影响听歌口味
            JSONObject resourceJSON = mapi.getRecommendedPlay(user.getCookie());
            //解析歌单，返回一个map，key是歌单id,value是歌单中歌曲id的集合
            Map<Long, List<Long>> playListMap = mapi.parsePlayList(resourceJSON, user.getCookie());
            Set<Long> playListKeySet = playListMap.keySet();
            a:
            for (Long sourceId : playListKeySet) {
                List<Long> songIdList = playListMap.get(sourceId);
                for (Long songId : songIdList) {
                    try {
                        JSONObject listenResult = mapi.listenSong(user.getCookie(), songId + "", sourceId + "");
                        if (listenResult != null && listenResult.getInteger("code") == 200) {
                            successCount++;
                            log.info(user.getName() + ": 第" + successCount + "首 : " + sourceId + "===>>歌曲ID : " + songId + "目标 ：" + upperLimit);
                            Thread.sleep(500);
                            //每100首检测一次
                            if (successCount >= 300 && successCount % 50 == 0) {
                                int t = Integer.parseInt(mapi.userLevel(user.getCookie()).getJSONObject("data").getString("nowPlayCount"));
                                if (t == upperLimit | successCount > 2000) {
                                    log.info("已完成每日300首听歌当前等级听歌量为：{}", t);
                                    break a;
                                }
                                log.info("当前听歌量为 : {}首,未达到目标 : {}首", t, upperLimit);
                            }
                        }
                    } catch (Exception e) {
                        log.info("失败");
                    }
                }
            }
        } else {
            log.info("用户 {} 已满级，自动跳过！！！", user.getName());
        }
    }
}

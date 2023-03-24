package top.xmlsj.signin;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import top.xmlsj.signin.util.ExceptionConstants;
import top.xmlsj.signin.wangyi.api.MusicApiService;
import top.xmlsj.signin.wangyi.domain.entity.MusicUser;
import top.xmlsj.signin.wangyi.domain.entity.WangYiConfig;
import top.xmlsj.signin.wangyi.service.MusicUserService;
import top.xmlsj.signin.wangyi.service.WangYiCoreService;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
class ApplicationTests {

    @Resource
    private WangYiCoreService wangYiCoreService;

    @Resource
    private MusicApiService mapi;

    @Resource
    private MusicUserService userService;

    @Test
    void contextLoads() {
        userService.truncateTable();
        WangYiConfig config = wangYiCoreService.readWangYiConfig();
        List<MusicUser> accounts = config.getAccounts();
        Assert.notNull(accounts, ExceptionConstants.ACCOUNTS_NULL);
        //入库
        System.out.println(accounts);
        userService.saveBatch(accounts);
        System.out.println(userService.list());
    }
}

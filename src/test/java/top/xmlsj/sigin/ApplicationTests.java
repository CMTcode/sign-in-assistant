package top.xmlsj.sigin;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import top.xmlsj.sigin.baidu.service.SingInService;


@SpringBootTest
class ApplicationTests {

    @Autowired
    private SingInService singInService;

    @Test
    void contextLoads() {
        singInService.autoSingin();
    }

}

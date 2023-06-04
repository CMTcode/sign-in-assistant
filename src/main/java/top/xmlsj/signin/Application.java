package top.xmlsj.signin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import top.xmlsj.signin.core.util.CoreUtil;
import top.xmlsj.signin.task.SignInTimedTasks;

/**
 * @author ForkManTou
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        // 数据初始化
        CoreUtil.initialize(applicationContext);
        // 运行一次签到线程
        applicationContext.getBean(SignInTimedTasks.class).start();
//

//
    }

}

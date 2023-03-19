package top.xmlsj.signin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import top.xmlsj.signin.task.MainTask;


/**
 * @author ForkManTou
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        // 签到线程
        applicationContext.getBean(MainTask.class).start();
    }

}

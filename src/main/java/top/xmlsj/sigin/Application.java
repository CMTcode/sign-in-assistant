package top.xmlsj.sigin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import top.xmlsj.sigin.task.MainTask;


/**
 * @author ForkManTou
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        // 签到线程
        applicationContext.getBean(MainTask.class).run();
    }

}

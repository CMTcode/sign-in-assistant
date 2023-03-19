package top.xmlsj.signin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created on 2023/3/6.
 *
 * @author Yang YaoWei
 */
@Configuration
public class ExecutorConfig {

    @Bean("baiduasync")
    public Executor baiduExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(2);
        //配置最大线程数
        executor.setMaxPoolSize(10);
        //配置队列大小
        executor.setQueueCapacity(100);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("百度贴吧 -");
        // 设置拒绝策略：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;

    }

    @Bean("bilbilasync")
    public Executor bilbilExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(100);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("哔哩哔哩 -");
        // 设置拒绝策略：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;

    }


    @Bean("wangyiasync")
    public Executor wangyiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(100);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("网易云音乐 -");
        // 设置拒绝策略：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;

    }
}

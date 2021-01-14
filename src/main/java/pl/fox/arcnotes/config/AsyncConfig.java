package pl.fox.arcnotes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "taskExecutor")
    public java.util.concurrent.Executor taskExecutor(){
        LOG.debug("Creating ASYNC TASK Executor");
        final ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(2);
        exec.setQueueCapacity(100);
        exec.setThreadNamePrefix("NoteThread-");
        exec.initialize();
        return exec;
    }


}

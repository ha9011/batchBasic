package com.example.SpringBatchTutorial.job.jobListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/*
 * Listener (Before, after Log)
 * --spring.batch.job.names=jobListenerJob
 * */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobListenerConfig{

    private final JobBuilderFactory jobBuilderFactory; // Job 생성

    private final StepBuilderFactory stepBuilderFactory; // Step 생성
    /*
     * 두 객체를 활용하여 Job과 Step을 생성할꺼야
     Job은 JobBuilderFactory로 만들고
     Step은 StepBuilderFactory로 만들자.
    * */

    @Bean
    public Job jobListenerJob(){
        return jobBuilderFactory.get("jobListenerJob")
                .incrementer(new RunIdIncrementer()) //Job을 실행할 때 id를 부여하는데, Sequence를 순차적으로 부여할 수 있도록 RunIdIncrementer를 해주자.
//                .listener(new JobExecutionListener() {
//                    @Override
//                    public void beforeJob(JobExecution jobExecution) {
//
//                    }
//
//                    @Override
//                    public void afterJob(JobExecution jobExecution) {
//
//                    }
//                })
                .listener(new JobLoggerListerner()) // 관리를 위해 호출
                .start(jobListenerStep())
                .build();

    }

    @JobScope
    @Bean
    public Step jobListenerStep(){
        return stepBuilderFactory.get("jobListenerStep")
                .tasklet(jobListenerTasklet()) // 읽고 작업하고 저장할게 없다면 그냥 tasklet을 이용
                .build();
    }

    @StepScope
    @Bean
    public Tasklet jobListenerTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                // 각 property들을 이용하는게 관건임(StepContribution contribution, ChunkContext chunkContext)
                System.out.println("jobListenerJob");
                contribution.setExitStatus(ExitStatus.FAILED);
                throw new Exception();
                //return RepeatStatus.FINISHED; // 작업결과 상태
            }
        };
    }
}

package com.example.SpringBatchTutorial.job.helloWorld;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * 기본 흐름
 * --spring.batch.job.names=helloWorldJob
 * */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class HelloWorldJobConfig {

    private final JobBuilderFactory jobBuilderFactory; // Job 생성

    private final  StepBuilderFactory stepBuilderFactory; // Step 생성
    /*
     * 두 객체를 활용하여 Job과 Step을 생성할꺼야
     Job은 JobBuilderFactory로 만들고
     Step은 StepBuilderFactory로 만들자.
    * */

    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob") // program argument 옵션에 추가 : --spring.batch.job.names=helloWorldJob // applcation.yml
                .incrementer(new RunIdIncrementer()) //Job을 실행할 때 id를 부여하는데, Sequence를 순차적으로 부여할 수 있도록 RunIdIncrementer를 해주자.
                .start(helloWorldStep())
                .build();

    }

    @JobScope
    @Bean
    public Step helloWorldStep(){
        return stepBuilderFactory.get("helloWorldStep")
                .tasklet(helloWorldTasklet()) // 읽고 작업하고 저장할게 없다면 그냥 tasklet을 이용
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                // 각 property들을 이용하는게 관건임(StepContribution contribution, ChunkContext chunkContext)
                System.out.println("Hello World Spring Batch");
                return RepeatStatus.FINISHED; // 작업결과 상태
            }
        };
    }
}

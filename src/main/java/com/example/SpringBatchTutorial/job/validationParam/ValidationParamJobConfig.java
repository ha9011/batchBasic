package com.example.SpringBatchTutorial.job.validationParam;

import com.example.SpringBatchTutorial.job.validationParam.validate.FileNameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/*
* 특정값 받는 방법
* --spring.batch.job.names=validatedParamJob --fileName=test.csv
* */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ValidationParamJobConfig {

    private final JobBuilderFactory jobBuilderFactory; // Job 생성

    private final  StepBuilderFactory stepBuilderFactory; // Step 생성

    private final FileNameValidator fileNameValidator;
    /*
     * 두 객체를 활용하여 Job과 Step을 생성할꺼야
     Job은 JobBuilderFactory로 만들고
     Step은 StepBuilderFactory로 만들자.
    * */

    @Bean
    public Job validatedParamJob(Step validatedParamStep) throws JobParametersInvalidException {
        return jobBuilderFactory.get("validatedParamJob") // program argument 옵션에 추가 : --spring.batch.job.names=helloWorldJob // applcation.yml
                .incrementer(new RunIdIncrementer()) //Job을 실행할 때 id를 부여하는데, Sequence를 순차적으로 부여할 수 있도록 RunIdIncrementer를 해주자.
                .validator(fileNameValidator) // 필요 파라미터가 자동으로 격납
                .start(validatedParamStep)
                .build();

    }

//    private CompositeJobParametersValidator multipleValidator() {
//        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
//        validator.setValidators(Arrays.asList(fileNameValidator, fileNameValidator2, fileNameValidator3));
//        // 여러 validator로 체크 하고 싶을 경우
//
//        return validator;
//    }


    @JobScope
    @Bean
    public Step validatedParamStep(Tasklet validatedParamTasklet ){
        return stepBuilderFactory.get("validatedParamStep")
                .tasklet(validatedParamTasklet) // 읽고 작업하고 저장할게 없다면 그냥 tasklet을 이용
                .build();
    }


    @StepScope
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                // 각 property들을 이용하는게 관건임(StepContribution contribution, ChunkContext chunkContext)
                System.out.println("Validated Param Batch");
                System.out.println("fileName : " + fileName);
                return RepeatStatus.FINISHED; // 작업결과 상태
            }
        };
    }
}

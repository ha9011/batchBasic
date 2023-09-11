package com.example.SpringBatchTutorial.job.fileDataReadWrite;

import com.example.SpringBatchTutorial.job.fileDataReadWrite.dto.Player;
import com.example.SpringBatchTutorial.job.fileDataReadWrite.dto.PlayerYears;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/*
 * 기본 흐름
 * --spring.batch.job.names=fileDataReadWriteJob
 * */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileDataReadWriteConfig {

    private final JobBuilderFactory jobBuilderFactory; // Job 생성

    private final StepBuilderFactory stepBuilderFactory; // Step 생성
    /*
     * 두 객체를 활용하여 Job과 Step을 생성할꺼야
     Job은 JobBuilderFactory로 만들고
     Step은 StepBuilderFactory로 만들자.
    * */

    @Bean
    public Job fileDataReadWriteJob(Step fileDataReadWriteStep){
        return jobBuilderFactory.get("fileDataReadWriteJob") // program argument 옵션에 추가 : --spring.batch.job.names=helloWorldJob // applcation.yml
                .incrementer(new RunIdIncrementer()) //Job을 실행할 때 id를 부여하는데, Sequence를 순차적으로 부여할 수 있도록 RunIdIncrementer를 해주자.
                .start(fileDataReadWriteStep)
                .build();

    }

    @JobScope
    @Bean
    public Step fileDataReadWriteStep(
            ItemReader playerItemReader,
            ItemProcessor playerItemProcessor,
            ItemWriter playerItemWriter
    ){
        return stepBuilderFactory.get("fileDataReadWriteStep")
                .<Player, PlayerYears>chunk(5)
                .reader(playerItemReader)
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .build();
    }

    @StepScope
    @Bean // FlatFileItemReader 파일 읽어오기
    public FlatFileItemReader<Player> playerItemReader(){
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("src/main/resources/players.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PlayerFieldSetMapper())
                .linesToSkip(1)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Player, PlayerYears> playerItemProcessor(){
        return new ItemProcessor<Player, PlayerYears>() {
            @Override
            public PlayerYears process(Player item) throws Exception {
                return PlayerYears.create(item);
            }
        };
    }

    @StepScope
    @Bean // FlatFileItemReader 파일 저장
    public FlatFileItemWriter<PlayerYears> playerItemWriter(){
        // 어떤 필드를 사용할지 명시 하기 위해 BeanWrapperFieldExtractor 만들어야함
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        // 축출할 필드 가져오기
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();
        // 어떤 기준으로 파일을 만들지 정하기
        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("src/main/resources/players_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

}
package com.example.SpringBatchTutorial.job.dbDataReadWrite;

import com.example.SpringBatchTutorial.core.domain.accounts.Accounts;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;
import lombok.NoArgsConstructor;
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
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 주문 테이블 -> 정산 테이블로 이동
 * --spring.batch.job.names=trMigrationJob
 * */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final OrdersRepository ordersRepository;


    private final AccountsRepository accountsRepository;


    private final JobBuilderFactory jobBuilderFactory;


    private final StepBuilderFactory stepBuilderFactory;
    int currentIndex = 0;
    @Bean
    public Job trMigrationJob(Step trMigrationStep){
        return jobBuilderFactory.get("trMigrationJob") // program argument 옵션에 추가 : --spring.batch.job.names=helloWorldJob // applcation.yml
                .incrementer(new RunIdIncrementer()) //Job을 실행할 때 id를 부여하는데, Sequence를 순차적으로 부여할 수 있도록 RunIdIncrementer를 해주자.
                .start(trMigrationStep)
                .build();

    }

    @JobScope
    @Bean
    public Step trMigrationStep(
            ItemReader trOrdersReader,
            ItemProcessor trOrdersProcessor,
            ItemWriter trOrdersWriter
    ){
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5) // chunk 단위는 한 트랜잭션에서 처리할 갯수 // <어떤데이터를 읽고, 어떤데이토로 쓸건지>
                .reader(trOrdersReader)
                .processor(trOrdersProcessor)
                .writer(trOrdersWriter)
                .build();
    }

    @StepScope
    @Bean // RepositoryItemReader -> json이냐, JDBC, Repository냐에 따라 다름
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)// chunk size와 동일하게
                //.arguments(Arrays.of())// 파라미터 있는 메소드면 필요함, 없는데도 필수인지 모르겠음
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrdersProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                System.out.println("----item info----");
                System.out.println(item.toString());
                System.out.println("-----------------");
                return Accounts.create(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trOrdersWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<Accounts> trOrdersWriter1() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean // RepositoryItemReader -> json이냐, JDBC, Repository냐에 따라 다름
    public ItemReader<Orders> trOrdersReader1() {

        List<Orders> ordersList = ordersRepository.findAll();
        System.out.println("order max id :"+ordersList.get(ordersList.size()-1).getId());
         // 현재 읽고 있는 인덱스

        return new ItemReader<Orders>() {
            @Override
            public Orders read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                // 현재 인덱스가 ordersList의 크기를 넘어가면 더 이상 읽을 데이터가 없음

                // 알아서 chunk 만큼 5개가 호출됨, 그리고 1개씩 리턴해주면됨
                // 마지막에 갯수 체크해서 null만 보내면 문제 없음
                     // 다음 인덱스로 이동
                    if(currentIndex < ordersList.size()-1){
                        System.out.println(currentIndex);
                        currentIndex++;
                        return ordersList.get(currentIndex);
                    }else{
                        return null;
                    }

            }
        };


    }
}

package com.example.SpringBatchTutorial.job.jobListener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListerner implements JobExecutionListener {
    private static String BEFORE_MESSAGE = "{} JOB IS RUNNING!!";
    private static String AFTER_MESSAGE = "{} JOB IS DONE. (Status : {}}";
    @Override
    public void beforeJob(JobExecution jobExecution){
        log.info(BEFORE_MESSAGE, jobExecution.getJobInstance().getJobName());
    };

    @Override
    public void afterJob(JobExecution jobExecution){
        log.info(AFTER_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
        //jobExecution.getStatus() - 현재 진행 상태
        //jobExecution.getExitStatus() - 종료 후 성공, 실패
        // 차이가 뭘까?
        if(jobExecution.getStatus() == BatchStatus.FAILED){
            // email 전송
            log.info("이메일 전송 - Job is Fail");
        }
    };
}

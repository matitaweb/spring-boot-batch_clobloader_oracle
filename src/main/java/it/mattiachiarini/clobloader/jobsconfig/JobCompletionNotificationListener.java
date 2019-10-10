package it.mattiachiarini.clobloader.jobsconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/*
* Listener che viene attivato quando il job è in stato completato.
* Misuro quanto ha impegato in secondi
* */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	//@Qualifier("taskExecutor")
	//@Autowired
	//private SimpleAsyncTaskExecutor taskExecutor;


	@Override
	public void beforeJob(JobExecution jobExecution)
	{
		// do nothing
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");
		}

		Date start = jobExecution.getCreateTime();

		//  get job's end time
		Date end = jobExecution.getEndTime();

		// get diff between end time and start time
		long diff = end.getTime() - start.getTime();

		// log diff time
		log.info("TOT TIME JOB, sec:"+ TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS));

		//taskExecutor.shutdown();
	}
	
}

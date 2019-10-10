package it.mattiachiarini.clobloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;


/*
 * Prendo a riferimento
 * https://github.com/eugenp/tutorials/tree/master/spring-jooq/src
 *
 * */

@SpringBootApplication
@EnableTransactionManagement
public class ClobloaderApplication implements ApplicationRunner {

    // TO LOG
    private static final Logger logger = LoggerFactory.getLogger(ClobloaderApplication.class);


    @Autowired
    JobLauncher jobLauncher; /* punto di partenza che lancia i job*/

    @Autowired
    Job kpJob; /* job principale */


    public static void main(String[] args) {
        SpringApplication.run(ClobloaderApplication.class, args);

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("# NonOptionArgs: " + args.getNonOptionArgs().size());

        /*
        * PARAMETRI PASSATI CON LA CMD/BASH
        * https://codeboje.de/spring-boot-commandline-app-args/
        * https://www.baeldung.com/java-run-jar-with-arguments
        * */
        String source_dir = "./file_to_upload_100";
        String sql_statement = "INSERT INTO X_LOB (ID, CAMPO_JSON) VALUES (:id, :value)";

        if(args.getNonOptionArgs().size()>1){
            source_dir = args.getNonOptionArgs().get(0);
            sql_statement = args.getNonOptionArgs().get(1);
        }
        if(args.getNonOptionArgs().size()==1){
            source_dir = args.getNonOptionArgs().get(0);
        }

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("source_dir", new JobParameter(source_dir));
        maps.put("sql_statement", new JobParameter(sql_statement));

        /*
        * Aggiunta per far partire il jop in ogni momento
        * https://www.mkyong.com/spring-batch/spring-batch-a-job-instance-already-exists-and-is-complete-for-parameters/
        * */
        maps.put("start_time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);

        try {
            JobExecution productRatingJobExecution = jobLauncher.run(kpJob, parameters);
            logger.info("product rating job execution completed, status : {} ", productRatingJobExecution.getExitStatus());

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {
            logger.error("Error message : {} ", e.getMessage());
        }


    }

}

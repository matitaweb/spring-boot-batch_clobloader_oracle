package it.mattiachiarini.clobloader.jobsconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/*
* CONFIGURAZIOBNE
*
* */

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

	@Value("${gridSize}")
	public int gridSize; // numero di partizioni che lavorano in parallelo


	@Value("${chunkSize}")
	public int chunkSize; // dimensione di ogni chunk


	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	public FlatFileItemReader<String> reader;

	@Autowired
	public ItemWriter writer;


	@Bean
	@Qualifier("taskExecutor")
	public SimpleAsyncTaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(gridSize*2);
		return taskExecutor;
	}

	@Autowired
	public MultiResourcePartitioner partitioner;


	/*
	* Si occupa di caricare i file e dividerli in lotti per
	* parallelizzare i task successivi
	* */
	@Bean
	@JobScope
	public MultiResourcePartitioner paritioner(@Value("#{jobParameters[source_dir]}") String source_dir) throws IOException {

		Set<String> strings = LobUtils.listFilesUsingJavaIO(source_dir);
		log.info("In Partitioner source_dir: " + source_dir +" tot file: " + strings.size());

		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		partitioner.setResources(strings.stream().map(s -> new FileSystemResource(Paths.get(source_dir, s))).toArray(Resource[]::new));
		partitioner.partition(gridSize);
		return partitioner;
	}

	@Bean
	@StepScope
	public FlatFileItemReader<String> reader(@Value("#{stepExecutionContext[fileName]}") Resource file) {
		FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
		reader.setResource(file);
		reader.setLineMapper(new PassThroughLineMapper());
		return reader;
	}

	@Bean
	@StepScope
	@Qualifier("writer")
	public JdbcBatchItemWriter<String> writer(DataSource dataSource, @Value("#{stepExecutionContext[fileName]}") Resource file, @Value("#{jobParameters[sql_statement]}") String sql_statement) {

		return new JdbcBatchItemWriterBuilder<String>()
				.itemSqlParameterSourceProvider(new CustomSqlParameterSourceProvider(file))
				.sql(sql_statement)
				.dataSource(dataSource)
				.build();
	}

	@Bean
	public Job kpJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("kpJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.partitioner(slaveStep()) // associa lo step slave che dovra' essere alimentato dal partitioner
				.partitioner("step1.slave", partitioner) //antepone allo step slave ip partitioner
				.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public Step slaveStep() {
		return stepBuilderFactory.get("step1.slave")
				.<String, String>chunk(chunkSize)
				.reader(reader)
				.writer(writer)
				.build();
	}

}


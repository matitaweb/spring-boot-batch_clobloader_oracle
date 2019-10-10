package it.mattiachiarini.clobloader.jobsconfig;

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.HashMap;

/*
*  Procider che permette di associare ai placeholder
* https://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-writing-information-to-a-database-with-jdbc/
* */
public class CustomSqlParameterSourceProvider implements ItemSqlParameterSourceProvider<String> {

    private Resource file;

    public CustomSqlParameterSourceProvider(Resource file) {
        this.file = file;

    }

    @Override
    public SqlParameterSource createSqlParameterSource(final String item) {
        return new MapSqlParameterSource(new HashMap<String, Object>() {

            {
                final String[] split = file.getFilename().split("\\.");
                final Integer id = Integer.valueOf(split[0]);
                put("id", id);
                put("value", item);
            }
        });
    }
}

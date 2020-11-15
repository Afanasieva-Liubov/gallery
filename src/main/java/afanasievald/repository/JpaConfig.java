package afanasievald.repository;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    @Bean
    public DataSource getDataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/db_example?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC");
        dataSourceBuilder.username("root");
        String password = System.getenv("MYSQL_DATASOURCE_PASSWORD");
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}


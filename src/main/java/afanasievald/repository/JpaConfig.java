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
        dataSourceBuilder.password("1.3.3.3-,07333f");
        return dataSourceBuilder.build();
    }
}
/*spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/db_example?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
        spring.datasource.username=root
        spring.datasource.password=1.3.3.3-,07333f*/

package afanasievald.repository;

import afanasievald.uploadingPhoto.StorageProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    @Bean
    public DataSource getDataSource(StorageProperties storageProperties)
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(storageProperties.getDatasourceUrl());
        dataSourceBuilder.username(storageProperties.getDatasourceUserName());
        String password = System.getenv(storageProperties.getDatasourcePassword());
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}


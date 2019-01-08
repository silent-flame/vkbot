package silentflame.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataBaseConfiguration {
  @Bean
  DataSource getDataSource(
    @Value("${database.driverClassname}")
      String driverClassName,
    @Value("${database.url}")
      String url,
    @Value("${database.username}")
      String userName,
    @Value("${database.password}")
      String password) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driverClassName);
    dataSource.setUrl(url);
    dataSource.setUsername(userName);
    dataSource.setPassword(password);
    return dataSource;
  }
}

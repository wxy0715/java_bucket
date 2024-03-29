package com.wxy.seata1.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DatabaseIdBean {
    @Autowired
    DataSource dataSource;
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider(){
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("MySQL","mysql");
        properties.setProperty("MariaDB","mysql");
        properties.setProperty("DM DBMS","dm");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}

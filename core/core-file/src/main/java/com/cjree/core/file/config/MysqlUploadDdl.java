package com.cjree.core.file.config;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Order(value = 1)
public class MysqlUploadDdl implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MysqlUploadDdl.class);

    @Autowired
    private DataSource dataSource;

    @Value("${mybatis-plus.configuration.database-id:mysql}")
    private String databaseId;

    public  void runScript(Connection connection, boolean autoCommit) throws SQLException {
        ScriptRunner scriptRunner = getScriptRunner(connection, autoCommit);
        List<String> sqlFiles = new ArrayList<>();
        if ("mysql".equals(databaseId)) {
            sqlFiles = Arrays.asList(
                    "db/fileUpload.sql"
            );
        } else if ("dm".equals(databaseId)) {
            sqlFiles = Arrays.asList(
                    "db/fileUploadDM.sql"
            );
        }
        for (String sqlFile : sqlFiles) {
            try {
                log.info("run script file: {}", sqlFile);
                File file = new File(sqlFile);
                if (file.exists()) {
                    scriptRunner.runScript(new FileReader(file));
                } else {
                    scriptRunner.runScript(new InputStreamReader(getInputStream(sqlFile)));
                }
            } catch (Exception var12) {
                log.error("run script sql:{} , error: {}", sqlFile, var12.getMessage());
            }
        }

    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            runScript(connection, true);
        } catch (Exception var17) {
            log.error("run script error: {}", var17.getMessage());
        }
    }

    public static InputStream getInputStream(String path) throws Exception {
        return (new ClassPathResource(path)).getInputStream();
    }

    public static ScriptRunner getScriptRunner(Connection connection, boolean autoCommit) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setAutoCommit(autoCommit);
        scriptRunner.setStopOnError(false);
        return scriptRunner;
    }

}
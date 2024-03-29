package io.seata.sqlparser.druid;

import com.alibaba.druid.util.JdbcUtils;
import io.seata.sqlparser.util.DbTypeParser;

public class DruidDbTypeParserImpl implements DbTypeParser {
    DruidDbTypeParserImpl() {
    }

    @Override
    public String parseFromJdbcUrl(String jdbcUrl) {
        String dbtype = JdbcUtils.getDbType(jdbcUrl, (String)null);
        if("dm".equals(dbtype)) {
            dbtype = "oracle";
        }
        return dbtype;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.seata.sqlparser.druid;

import com.alibaba.druid.util.JdbcUtils;
import io.seata.common.loader.LoadLevel;
import io.seata.sqlparser.util.DbTypeParser;

import java.lang.reflect.Constructor;

@LoadLevel(
        name = "druid"
)
public class DruidDelegatingDbTypeParser implements DbTypeParser {
     private DbTypeParser dbTypeParserImpl;

    public DruidDelegatingDbTypeParser() {
        this.setClassLoader(DruidIsolationClassLoader.get());
    }

    void setClassLoader(ClassLoader classLoader) {
        try {
            Class<?> druidDbTypeParserImplClass = classLoader.loadClass("io.seata.sqlparser.druid.DruidDbTypeParserImpl");
            Constructor<?> implConstructor = druidDbTypeParserImplClass.getDeclaredConstructor();
            implConstructor.setAccessible(true);

            try {
                this.dbTypeParserImpl = (DbTypeParser)implConstructor.newInstance();
            } finally {
                implConstructor.setAccessible(false);
            }

        } catch (Exception var8) {
            throw new RuntimeException(var8);
        }
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

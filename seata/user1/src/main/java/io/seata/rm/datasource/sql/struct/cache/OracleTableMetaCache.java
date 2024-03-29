//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.seata.rm.datasource.sql.struct.cache;

import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.common.loader.LoadLevel;
import io.seata.common.util.StringUtils;
import io.seata.rm.datasource.sql.struct.ColumnMeta;
import io.seata.rm.datasource.sql.struct.IndexMeta;
import io.seata.rm.datasource.sql.struct.IndexType;
import io.seata.rm.datasource.sql.struct.TableMeta;
import io.seata.rm.datasource.sql.struct.cache.AbstractTableMetaCache;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@LoadLevel(
        name = "oracle"
)
public class OracleTableMetaCache extends AbstractTableMetaCache {
    public OracleTableMetaCache() {
    }

    @Override
    protected String getCacheKey(Connection connection, String tableName, String resourceId) {
        StringBuilder cacheKey = new StringBuilder(resourceId);
        cacheKey.append(".");
        String[] tableNameWithSchema = tableName.split("\\.");
        String defaultTableName = tableNameWithSchema.length > 1 ? tableNameWithSchema[1] : tableNameWithSchema[0];
        if (defaultTableName.contains("\"")) {
            cacheKey.append(defaultTableName.replace("\"", ""));
        } else {
            cacheKey.append(defaultTableName.toUpperCase());
        }

        return cacheKey.toString();
    }

    @Override
    protected TableMeta fetchSchema(Connection connection, String tableName) throws SQLException {
        try {
            return this.resultSetMetaToSchema(connection.getMetaData(), tableName);
        } catch (SQLException var4) {
            throw var4;
        } catch (Exception var5) {
            throw new SQLException(String.format("Failed to fetch schema of %s", tableName), var5);
        }
    }

    private TableMeta resultSetMetaToSchema(DatabaseMetaData dbmd, String tableName) throws SQLException {
        TableMeta tm = new TableMeta();
        tm.setTableName(tableName);
        String[] schemaTable = tableName.split("\\.");
        String schemaName = schemaTable.length > 1 ? schemaTable[0] : dbmd.getConnection().getSchema();
        tableName = schemaTable.length > 1 ? schemaTable[1] : tableName;
        if (schemaName.contains("\"")) {
            schemaName = schemaName.replace("\"", "");
        } else {
            schemaName = schemaName.toUpperCase();
        }

        if (tableName.contains("\"")) {
            tableName = tableName.replace("\"", "");
        } else {
            tableName = tableName.toUpperCase();
        }

        ResultSet rsColumns = dbmd.getColumns("", schemaName, tableName, "%");
        Throwable var7 = null;

        try {
            ResultSet rsIndex = dbmd.getIndexInfo((String)null, schemaName, tableName, false, true);
            Throwable var9 = null;

            try {
                ResultSet rsPrimary = dbmd.getPrimaryKeys((String)null, schemaName, tableName);
                Throwable var11 = null;

                try {
                    while(rsColumns.next()) {
                        ColumnMeta col = new ColumnMeta();
                        col.setTableCat(rsColumns.getString("TABLE_CAT"));
                        col.setTableSchemaName(rsColumns.getString("TABLE_SCHEM"));
                        col.setTableName(rsColumns.getString("TABLE_NAME"));
                        col.setColumnName(rsColumns.getString("COLUMN_NAME"));
                        col.setDataType(rsColumns.getInt("DATA_TYPE"));
                        col.setDataTypeName(rsColumns.getString("TYPE_NAME"));
                        col.setColumnSize(rsColumns.getInt("COLUMN_SIZE"));
                        col.setDecimalDigits(rsColumns.getInt("DECIMAL_DIGITS"));
                        col.setNumPrecRadix(rsColumns.getInt("NUM_PREC_RADIX"));
                        col.setNullAble(rsColumns.getInt("NULLABLE"));
                        col.setRemarks(rsColumns.getString("REMARKS"));
                        col.setColumnDef(rsColumns.getString("COLUMN_DEF"));
                        col.setSqlDataType(rsColumns.getInt("SQL_DATA_TYPE"));
                        col.setSqlDatetimeSub(rsColumns.getInt("SQL_DATETIME_SUB"));
                        col.setCharOctetLength(rsColumns.getInt("CHAR_OCTET_LENGTH"));
                        col.setOrdinalPosition(rsColumns.getInt("ORDINAL_POSITION"));
                        col.setIsNullAble(rsColumns.getString("IS_NULLABLE"));
                        tm.getAllColumns().put(col.getColumnName(), col);
                    }

                    String indexName;
                    while(rsIndex.next()) {
                        indexName = rsIndex.getString("INDEX_NAME");
                        if (!StringUtils.isNullOrEmpty(indexName)) {
                            String colName = rsIndex.getString("COLUMN_NAME");
                            ColumnMeta col = (ColumnMeta)tm.getAllColumns().get(colName);
                            IndexMeta index;
                            if (tm.getAllIndexes().containsKey(indexName)) {
                                index = (IndexMeta)tm.getAllIndexes().get(indexName);
                                index.getValues().add(col);
                            } else {
                                index = new IndexMeta();
                                index.setIndexName(indexName);
                                index.setNonUnique(rsIndex.getBoolean("NON_UNIQUE"));
                                index.setIndexQualifier(rsIndex.getString("INDEX_QUALIFIER"));
                                index.setIndexName(rsIndex.getString("INDEX_NAME"));
                                index.setType(rsIndex.getShort("TYPE"));
                                index.setOrdinalPosition(rsIndex.getShort("ORDINAL_POSITION"));
                                index.setAscOrDesc(rsIndex.getString("ASC_OR_DESC"));
                                index.setCardinality(rsIndex.getInt("CARDINALITY"));
                                index.getValues().add(col);
                                if (!index.isNonUnique()) {
                                    index.setIndextype(IndexType.UNIQUE);
                                } else {
                                    index.setIndextype(IndexType.NORMAL);
                                }

                                tm.getAllIndexes().put(indexName, index);
                            }
                        }
                    }

                    while(rsPrimary.next()) {
                        indexName = rsPrimary.getString("PK_NAME");
                        if (tm.getAllIndexes().containsKey(indexName)) {
                            IndexMeta index = (IndexMeta)tm.getAllIndexes().get(indexName);
                            index.setIndextype(IndexType.PRIMARY);
                        }
                    }

                    if (!tm.getAllIndexes().isEmpty()) {
                        return tm;
                    } else {
                        throw new ShouldNeverHappenException(String.format("Could not found any index in the table: %s", tableName));
                    }
                } catch (Throwable var58) {
                    var11 = var58;
                    throw var58;
                } finally {
                    if (rsPrimary != null) {
                        if (var11 != null) {
                            try {
                                rsPrimary.close();
                            } catch (Throwable var57) {
                                var11.addSuppressed(var57);
                            }
                        } else {
                            rsPrimary.close();
                        }
                    }

                }
            } catch (Throwable var60) {
                var9 = var60;
                throw var60;
            } finally {
                if (rsIndex != null) {
                    if (var9 != null) {
                        try {
                            rsIndex.close();
                        } catch (Throwable var56) {
                            var9.addSuppressed(var56);
                        }
                    } else {
                        rsIndex.close();
                    }
                }

            }
        } catch (Throwable var62) {
            var7 = var62;
            throw var62;
        } finally {
            if (rsColumns != null) {
                if (var7 != null) {
                    try {
                        rsColumns.close();
                    } catch (Throwable var55) {
                        var7.addSuppressed(var55);
                    }
                } else {
                    rsColumns.close();
                }
            }

        }
    }
}

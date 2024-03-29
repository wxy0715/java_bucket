//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.seata.rm.datasource.sql.struct;

import io.seata.common.exception.NotSupportYetException;
import io.seata.common.util.CollectionUtils;
import io.seata.rm.datasource.ColumnUtils;

import java.util.*;
import java.util.Map.Entry;

public class TableMeta {
    private String tableName;
    private Map<String, ColumnMeta> allColumns = new LinkedHashMap();
    private Map<String, IndexMeta> allIndexes = new LinkedHashMap();

    public TableMeta() {
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ColumnMeta getColumnMeta(String colName) {
        return (ColumnMeta)this.allColumns.get(colName);
    }

    public Map<String, ColumnMeta> getAllColumns() {
        return this.allColumns;
    }

    public Map<String, IndexMeta> getAllIndexes() {
        return this.allIndexes;
    }

    public ColumnMeta getAutoIncreaseColumn() {
        Iterator var1 = this.allColumns.entrySet().iterator();

        ColumnMeta col;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            Entry<String, ColumnMeta> entry = (Entry)var1.next();
            col = (ColumnMeta)entry.getValue();
        } while(!"YES".equalsIgnoreCase(col.getIsAutoincrement()));

        return col;
    }

    public Map<String, ColumnMeta> getPrimaryKeyMap() {
        Map<String, ColumnMeta> pk = new HashMap();
        this.allIndexes.forEach((key, index) -> {
            if (index.getIndextype().value() == IndexType.PRIMARY.value()
                    || (index.getIndextype().value() == IndexType.UNIQUE.value() && "ID".equalsIgnoreCase(index.getValues().get(0).getColumnName()))) {
                Iterator var3 = index.getValues().iterator();

                while(var3.hasNext()) {
                    ColumnMeta col = (ColumnMeta)var3.next();
                    pk.put(col.getColumnName(), col);
                }
            }

        });
        if (pk.size() < 1) {
            throw new NotSupportYetException(String.format("%s needs to contain the primary key.", this.tableName));
        } else {
            return pk;
        }
    }

    public List<String> getPrimaryKeyOnlyName() {
        List<String> list = new ArrayList();
        Iterator var2 = this.getPrimaryKeyMap().entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, ColumnMeta> entry = (Entry)var2.next();
            list.add(entry.getKey());
        }

        return list;
    }

    public List<String> getEscapePkNameList(String dbType) {
        return ColumnUtils.addEscape(this.getPrimaryKeyOnlyName(), dbType);
    }

    public boolean containsPK(List<String> cols) {
        if (cols == null) {
            return false;
        } else {
            List<String> pk = this.getPrimaryKeyOnlyName();
            if (pk.isEmpty()) {
                return false;
            } else {
                return cols.containsAll(pk) ? true : CollectionUtils.toUpperList(cols).containsAll(CollectionUtils.toUpperList(pk));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof TableMeta)) {
            return false;
        } else {
            TableMeta tableMeta = (TableMeta)o;
            if (!Objects.equals(tableMeta.tableName, this.tableName)) {
                return false;
            } else if (!Objects.equals(tableMeta.allColumns, this.allColumns)) {
                return false;
            } else {
                return Objects.equals(tableMeta.allIndexes, this.allIndexes);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = Objects.hashCode(this.tableName);
        hash += Objects.hashCode(this.allColumns);
        hash += Objects.hashCode(this.allIndexes);
        return hash;
    }
}

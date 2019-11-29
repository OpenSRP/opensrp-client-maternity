package org.smartregister.opd.pojos;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-16
 */

public class QueryTable {

    private String tableName;
    private String[] colNames;
    private String mainCondition = "";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getColNames() {
        return colNames;
    }

    public void setColNames(String[] colNames) {
        this.colNames = colNames;
    }

    public String getMainCondition() {
        return mainCondition;
    }

    public void setMainCondition(String mainCondition) {
        this.mainCondition = mainCondition;
    }
}

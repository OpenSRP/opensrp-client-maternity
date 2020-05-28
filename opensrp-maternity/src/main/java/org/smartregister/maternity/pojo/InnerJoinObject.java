package org.smartregister.maternity.pojo;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class InnerJoinObject {

    private QueryTable firstTable;
    private QueryTable secondTable;
    private String innerJoinClause;
    private String mainCondition = "";

    public QueryTable getFirstTable() {
        return firstTable;
    }

    public void setFirstTable(QueryTable firstTable) {
        this.firstTable = firstTable;
    }

    public void innerJoinOn(String innerJoinClause) {
        this.innerJoinClause = innerJoinClause;
    }

    public void innerJoinTable(QueryTable secondTable) {
        this.secondTable = secondTable;
    }

    public QueryTable getSecondTable() {
        return secondTable;
    }

    public String getInnerJoinClause() {
        return innerJoinClause;
    }

    public String getMainCondition() {
        return mainCondition;
    }

    public void setMainCondition(String mainCondition) {
        this.mainCondition = mainCondition;
    }
}

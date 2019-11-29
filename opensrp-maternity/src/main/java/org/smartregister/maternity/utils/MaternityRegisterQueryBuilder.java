package org.smartregister.maternity.utils;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterQueryBuilder extends SmartRegisterQueryBuilder {

    @Override
    public String SelectInitiateMainTableCounts(String tableName) {
        String selectQuery = "SELECT COUNT(*) as sub_count";
        selectQuery = selectQuery + " FROM " + tableName;

        setSelectquery(selectQuery);
        return selectQuery;
    }
}

package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.InnerJoinObject;
import org.smartregister.opd.pojos.QueryTable;
import org.smartregister.opd.utils.OpdRegisterQueryBuilder;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-23
 */

public abstract class OpdRegisterQueryProviderContract {

    /**
     * Return query to be used to select object_ids from the search table so that these objects_ids
     * are later used to retrieve the actual rows from the normal(non-FTS) table
     *
     * @param filters This is the search phrase entered in the search box
     * @return
     */
    @NonNull
    public abstract String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition);

    /**
     * Return query(s) to be used to perform the total count of register clients eg. If OPD combines records
     * in multiple tables then you can provide multiple queries with the result having a single row+column
     * and the counts will be summed up. Kindly try to use the search tables wherever possible.
     *
     * @param filters This is the search phrase entered in the search box
     * @return
     */
    @NonNull
    public abstract String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition);

    /**
     * Return query to be used to retrieve the client details. This query should have a "WHERE base_entity_id IN (%s)" clause where
     * the comma-separated  base-entity-ids for the clients will be inserted into the query and later
     * executed
     *
     * @return
     */
    @NonNull
    public abstract String mainSelectWhereIDsIn();

    /**
     * Generates a query that is returned in {@link #mainSelectWhereIDsIn()} in case you are using
     * an inner join and UNION from another table
     *
     * @param tableColsInnerJoins
     * @param tableCols
     * @return
     */
    public String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < tableColsInnerJoins.length; i++) {
            InnerJoinObject tableColInnerJoin = tableColsInnerJoins[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableColInnerJoin);
            countQueryBuilder.mainCondition(tableColInnerJoin.getMainCondition());

            String idCol = "_id";
            if (countQueryBuilder.getSelectquery().contains("JOIN")) {
                idCol = tableColInnerJoin.getFirstTable().getTableName() + ".id";
            }

            if (countQueryBuilder.getSelectquery().contains("WHERE")) {
                countQueryBuilder.addCondition(" AND ");

            } else {
                countQueryBuilder.addCondition(" WHERE ");
            }
            countQueryBuilder.addCondition("%s IN (%s)");
            countQueryBuilder.setSelectquery(countQueryBuilder.getSelectquery().replaceFirst("%s", idCol));

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }


        for (int i = 0; i < tableCols.length; i++) {
            QueryTable tableCol = tableCols[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableCol.getTableName(), tableCol.getColNames());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            String idCol = "_id";
            if (countQueryBuilder.getSelectquery().contains("JOIN")) {
                idCol = tableCol.getTableName() + ".id";
            }

            if (countQueryBuilder.getSelectquery().contains("WHERE")) {
                countQueryBuilder.addCondition(" AND ");

            } else {
                countQueryBuilder.addCondition(" WHERE ");
            }
            countQueryBuilder.addCondition("%s IN (%s)");
            countQueryBuilder.setSelectquery(countQueryBuilder.getSelectquery().replaceFirst("%s", idCol));

            if (query.length() != 0 || i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        return query.toString();
    }

    public String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < tableColsInnerJoins.length; i++) {
            InnerJoinObject tableColInnerJoin = tableColsInnerJoins[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableColInnerJoin);
            countQueryBuilder.mainCondition(tableColInnerJoin.getMainCondition());

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        for (int i = 0; i < tableCols.length; i++) {
            QueryTable tableCol = tableCols[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableCol.getTableName(), tableCol.getColNames());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            if (query.length() != 0 || i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        return query.toString();
    }
}

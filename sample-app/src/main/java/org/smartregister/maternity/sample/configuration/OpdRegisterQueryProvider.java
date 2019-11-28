package org.smartregister.maternity.sample.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.maternity.configuration.OpdRegisterQueryProviderContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-23
 */

public class OpdRegisterQueryProvider extends OpdRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        if (TextUtils.isEmpty(filters)) {
            return "SELECT object_id, last_interacted_with FROM ec_client_search" +
                    "ORDER BY last_interacted_with DESC";
        } else {
            String sql =
                    "SELECT object_id, last_interacted_with FROM ec_client_search WHERE date_removed IS NULL AND phrase MATCH '%s*'" +
                    "ORDER BY last_interacted_with DESC";
            sql = sql.replace("%s", filters);
            return sql;
        }
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

        return new String[] {
                sqb.countQueryFts("ec_client", null, null, filters)
        };
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return "Select ec_client.id as _id , first_name , last_name , '' AS middle_name , gender , dob , '' AS home_address , 'Maternity' AS register_type , relationalid , opensrp_id AS register_id , last_interacted_with , 'ec_client' as entity_table FROM ec_client" +
                "WHERE  ec_client.id IN (%s)\n" +
                "ORDER BY last_interacted_with DESC";
    }
}

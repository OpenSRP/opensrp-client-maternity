package org.smartregister.maternity.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MaternityRegisterQueryProviderTest extends MaternityRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        return null;
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        return new String[0];
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return null;
    }
}
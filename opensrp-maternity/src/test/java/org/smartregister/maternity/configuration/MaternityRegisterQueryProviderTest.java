package org.smartregister.maternity.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
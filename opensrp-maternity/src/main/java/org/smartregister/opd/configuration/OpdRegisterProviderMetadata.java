package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-19
 */

public interface OpdRegisterProviderMetadata {

    @NonNull
    String getGuardianFirstName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getGuardianMiddleName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getGuardianLastName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getClientFirstName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getClientMiddleName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getClientLastName(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getDob(@NonNull Map<String, String> columnMaps);

    boolean isClientHaveGuardianDetails(@NonNull Map<String, String> columnMaps);

    @Nullable
    String getRegisterType(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getHomeAddress(@NonNull Map<String, String> columnMaps);

    @NonNull
    String getGender(@NonNull Map<String, String> columnMaps);
}

package org.smartregister.maternity.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.maternity.provider.MaternityRegisterProvider;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.util.Utils;

import java.util.Map;

/**
 * This is a metadata class for the RegisterProvider at {@link MaternityRegisterProvider}. Some of the methods avoid null-checking but scream NotNullable
 * because https://github.com/OpenSRP/opensrp-client-core/blob/master/opensrp-app/src/main/java/org/smartregister/util/Utils.java#L208 checks for nulls and replaces them with empty strings
 * <p>
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BaseMaternityRegisterProviderMetadata implements MaternityRegisterProviderMetadata {


    @NonNull
    @Override
    public String getGuardianFirstName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.MOTHER_FIRST_NAME, true);
    }

    @NonNull
    @Override
    public String getGuardianMiddleName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.MOTHER_LAST_NAME, true);
    }

    @NonNull
    @Override
    public String getGuardianLastName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.MOTHER_MIDDLE_NAME, true);
    }

    @NonNull
    @Override
    public String getClientFirstName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.FIRST_NAME, true);
    }

    @NonNull
    @Override
    public String getClientMiddleName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.MIDDLE_NAME, true);
    }

    @NonNull
    @Override
    public String getClientLastName(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.LAST_NAME, true);
    }

    @NonNull
    @Override
    public String getDob(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.DOB, false);
    }

    @Override
    public boolean isClientHaveGuardianDetails(@NonNull Map<String, String> columnMaps) {
        String registerType = getRegisterType(columnMaps);
        return registerType != null && registerType.contains("Child");
    }

    @Nullable
    @Override
    public String getRegisterType(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.REGISTER_TYPE, true);
    }

    @NonNull
    @Override
    public String getHomeAddress(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.HOME_ADDRESS, true);
    }

    @NonNull
    @Override
    public String getGender(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.GENDER, true);
    }

    @NonNull
    public String getSafeValue(@Nullable String nullableString) {
        return nullableString == null ? "" : nullableString;
    }
}

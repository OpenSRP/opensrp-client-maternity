package org.smartregister.maternity.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.provider.MaternityRegisterProvider;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.util.Utils;

import java.text.SimpleDateFormat;
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

    @NonNull
    @Override
    public String getGA(@NonNull Map<String, String> columnMaps) {
        String gaInWeeks = getString(R.string.zero_weeks);
        String conceptionDateString = Utils.getValue(columnMaps, MaternityDbConstants.KEY.CONCEPTION_DATE, false);

        if (!TextUtils.isEmpty(conceptionDateString)) {
            DateTimeFormatter SQLITE_DATE_DF = DateTimeFormat.forPattern("dd-MM-yyyy");
            LocalDate conceptionDate = SQLITE_DATE_DF.withOffsetParsed().parseLocalDate(conceptionDateString);
            Weeks weeks = Weeks.weeksBetween(conceptionDate, LocalDate.now());
            int intWeeks = weeks.getWeeks();
            String weekString;

            if (intWeeks != 1) {
                weekString = getString(R.string.weeks);
            } else {
                weekString = getString(R.string.week);
            }

            gaInWeeks =  intWeeks + " " + weekString;
        }

        return gaInWeeks;
    }

    @NonNull
    @Override
    public String getPatientID(@NonNull Map<String, String> columnMaps) {
        return Utils.getValue(columnMaps, MaternityDbConstants.KEY.REGISTER_ID, true);
    }

    @NonNull
    public String getSafeValue(@Nullable String nullableString) {
        return nullableString == null ? "" : nullableString;
    }

    @NonNull
    private String getString(@StringRes int stringResId) {
        return MaternityLibrary.getInstance().context().applicationContext().getString(stringResId);
    }
}

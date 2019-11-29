package org.smartregister.opd.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdUtils extends org.smartregister.util.Utils {

    private static final String OTHER_SUFFIX = ", other]";
    private static FormUtils formUtils;

    public static float convertDpToPixel(float dp, @NonNull Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @NonNull
    public static String fillTemplate(boolean isHtml, @NonNull String stringValue, @NonNull Facts facts) {
        String stringValueResult = stringValue;
        while (stringValueResult.contains("{")) {
            String key = stringValueResult.substring(stringValueResult.indexOf("{") + 1, stringValueResult.indexOf("}"));
            String value = processValue(key, facts);
            stringValueResult = stringValueResult.replace("{" + key + "}", value).replaceAll(", $", "").trim();
        }

        //Remove unnecessary commas by cleaning the returned string
        return isHtml ? stringValueResult : cleanValueResult(stringValueResult);
    }

    public static String fillTemplate(@NonNull String stringValue, @NonNull Facts facts) {
        return fillTemplate(false, stringValue, facts);
    }

    public static boolean isTemplate(@NonNull String stringValue) {
        return stringValue.contains("{") && stringValue.contains("}");
    }

    public static void setTextAsHtml(@NonNull TextView textView, @NonNull String html) {
        textView.setAllCaps(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(html));
        }
    }

    @NonNull
    private static String processValue(@NonNull String key, @NonNull Facts facts) {
        String value = "";
        if (facts.get(key) instanceof String) {
            value = facts.get(key);
            if (value != null && value.endsWith(OTHER_SUFFIX)) {
                Object otherValue = value.endsWith(OTHER_SUFFIX) ? facts.get(key + ConstantsUtils.SuffixUtils.OTHER) : "";
                value = otherValue != null ?
                        value.substring(0, value.lastIndexOf(",")) + ", " + otherValue.toString() + "]" :
                        value.substring(0, value.lastIndexOf(",")) + "]";

            }
        }

        return keyToValueConverter(value);
    }

    @NonNull
    private static String cleanValueResult(@NonNull String result) {
        List<String> nonEmptyItems = new ArrayList<>();

        for (String item : result.split(",")) {
            if (item.length() > 1) {
                nonEmptyItems.add(item);
            }
        }
        //Get the first item that usually  has a colon and remove it form list, if list has one item append separator
        String itemLabel = "";
        if (!nonEmptyItems.isEmpty() && nonEmptyItems.get(0).contains(":")) {
            String[] separatedLabel = nonEmptyItems.get(0).split(":");
            itemLabel = separatedLabel[0];
            if (separatedLabel.length > 1) {
                nonEmptyItems.set(0, nonEmptyItems.get(0).split(":")[1]);
            }//replace with extracted value
        }
        return itemLabel + (!TextUtils.isEmpty(itemLabel) ? ": " : "") + StringUtils.join(nonEmptyItems.toArray(), ",");
    }

    @NonNull
    public static org.smartregister.Context context() {
        return OpdLibrary.getInstance().context();
    }

    @Nullable
    public static OpdMetadata metadata() {
        return OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
    }

    @Nullable
    public static String getIntentValue(@Nullable Intent data, @NonNull String key) {
        if (data == null) {
            return null;
        }

        return data.hasExtra(key) ? data.getStringExtra(key) : null;
    }

    @NonNull
    public static String convertDate(@NonNull Date date, @NonNull String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        return simpleDateFormat.format(date);
    }

    @Nullable
    public static Date convertStringToDate(@NonNull String pattern, @NonNull String dateString) {
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);

        if (!TextUtils.isEmpty(dateString) && !TextUtils.isEmpty(pattern)) {
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }
        return date;
    }

    @NonNull
    public static String generateNIds(int n) {
        StringBuilder strIds = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if ((i + 1) == n) {
                strIds.append(JsonFormUtils.generateRandomUUIDString());
            } else {
                strIds.append(JsonFormUtils.generateRandomUUIDString()).append(",");
            }
        }
        return strIds.toString();
    }

    @NotNull
    public static String getClientAge(String dobString, String translatedYearInitial) {
        String age = dobString;
        if (dobString.contains(translatedYearInitial)) {
            String extractedYear = dobString.substring(0, dobString.indexOf(translatedYearInitial));
            int year = dobString.contains(translatedYearInitial) ? Integer.parseInt(extractedYear) : 0;
            if (year >= 5) {
                age = extractedYear;
            }
        }
        return age;
    }

    @NonNull
    public static Intent buildFormActivityIntent(JSONObject jsonForm, HashMap<String, String> parcelableData, Context context) {
        Intent intent = new Intent(context, OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getOpdFormActivity());
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setWizard(false);
        form.setName("");
        String encounterType = jsonForm.optString(OpdJsonFormUtils.ENCOUNTER_TYPE);
        if (encounterType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
            form.setName(OpdConstants.EventType.DIAGNOSIS_AND_TREAT);
            form.setWizard(true);
        }
        form.setHideSaveLabel(true);
        form.setPreviousLabel("");
        form.setNextLabel("");
        form.setHideNextButton(false);
        form.setHidePreviousButton(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        if (parcelableData != null) {
            for (String intentKey : parcelableData.keySet()) {
                intent.putExtra(intentKey, parcelableData.get(intentKey));
            }
        }
        return intent;
    }

    @Nullable
    public static JSONObject getJsonFormToJsonObject(String formName){
        if (getFormUtils() == null) {
            return null;
        }

        return getFormUtils().getFormJson(formName);
    }


    @Nullable
    private static FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(OpdLibrary.getInstance().context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }


    @NonNull
    public static String keyToValueConverter(String keys) {
        if (!TextUtils.isEmpty(keys)) {
            String cleanKey;

            //If this contains html then don't capitalize it because it fails and the output is in lowercase
            if (keys.contains("<") && keys.contains(">")) {
                cleanKey = keys;
            } else {
                cleanKey = WordUtils.capitalizeFully(cleanValue(keys), ',');
            }

            return cleanKey.replaceAll("_", " ");
        } else {
            return "";
        }
    }

    public static String cleanValue(String raw) {
        if (raw.length() > 0 && raw.charAt(0) == '[') {
            return raw.substring(1, raw.length() - 1);
        } else {
            return raw;
        }
    }

}

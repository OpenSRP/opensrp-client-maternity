package org.smartregister.maternity.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.pojo.MaternityOutcomeForm;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityUtils extends org.smartregister.util.Utils {

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
            if (item.length() > 0) {
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
        return MaternityLibrary.getInstance().context();
    }

    @Nullable
    public static MaternityMetadata metadata() {
        return MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityMetadata();
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

    @Nullable
    public static Intent buildFormActivityIntent(JSONObject jsonForm, HashMap<String, String> intentData, Context context) {
        MaternityMetadata maternityMetadata = MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityMetadata();
        if (maternityMetadata != null) {
            Intent intent = new Intent(context, maternityMetadata.getMaternityFormActivity());
            intent.putExtra(MaternityConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            Form form = new Form();
            form.setWizard(false);
            form.setName("");
            String encounterType = jsonForm.optString(MaternityJsonFormUtils.ENCOUNTER_TYPE);
            form.setName(encounterType);

            // If the form has more than one step, enable the form wizard
            for (Iterator<String> objectKeys = jsonForm.keys(); objectKeys.hasNext(); ) {
                String key = objectKeys.next();
                if (!TextUtils.isEmpty(key) && key.contains("step") && !"step2".equalsIgnoreCase(key)) {
                    form.setWizard(true);
                    break;
                }
            }

            form.setHideSaveLabel(true);
            form.setPreviousLabel("");
            form.setNextLabel("");
            form.setHideNextButton(false);
            form.setHidePreviousButton(false);

            if (MaternityConstants.EventType.MATERNITY_OUTCOME.equals(jsonForm.optString(MaternityConstants.JSON_FORM_KEY.ENCOUNTER_TYPE))) {
                form.setSaveLabel(context.getString(R.string.submit_and_close_maternity));
            }

            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            if (intentData != null) {
                for (String intentKey : intentData.keySet()) {
                    intent.putExtra(intentKey, intentData.get(intentKey));
                }
            }
            return intent;
        }

        return null;
    }

    @Nullable
    public static JSONObject getJsonFormToJsonObject(String formName) {
        if (getFormUtils() == null) {
            return null;
        }

        return getFormUtils().getFormJson(formName);
    }


    @Nullable
    public static FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(MaternityLibrary.getInstance().context().applicationContext());
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

    @NonNull
    public static JSONArray generateFieldsFromJsonForm(@NonNull JSONObject jsonFormObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        Iterator<String> formKeys = jsonFormObject.keys();

        while (formKeys.hasNext()) {
            String formKey = formKeys.next();
            if (formKey != null && formKey.startsWith("step")) {
                JSONObject stepJSONObject = jsonFormObject.getJSONObject(formKey);
                JSONArray fieldsArray = stepJSONObject.getJSONArray(MaternityJsonFormUtils.FIELDS);
                for (int i = 0; i < fieldsArray.length(); i++) {
                    jsonArray.put(fieldsArray.get(i));
                }
            }
        }

        return jsonArray;
    }

    public static HashMap<String, HashMap<String, String>> buildRepeatingGroupValues(@NonNull JSONObject stepJsonObject, String fieldName) throws JSONException {
        ArrayList<String> keysArrayList = new ArrayList<>();
        JSONArray fields = stepJsonObject.optJSONArray(JsonFormConstants.FIELDS);
        JSONObject jsonObject = JsonFormUtils.getFieldJSONObject(fields, fieldName);
        HashMap<String, HashMap<String, String>> repeatingGroupMap = new HashMap<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray(JsonFormConstants.VALUE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject valueField = jsonArray.optJSONObject(i);
                String fieldKey = valueField.optString(JsonFormConstants.KEY);
                keysArrayList.add(fieldKey);
            }

            for (int k = 0; k < fields.length(); k++) {
                JSONObject valueField = fields.optJSONObject(k);
                String fieldKey = valueField.optString(JsonFormConstants.KEY);
                String fieldValue = valueField.optString(JsonFormConstants.VALUE);

                if (fieldKey.contains("_")) {
                    fieldKey = fieldKey.substring(0, fieldKey.lastIndexOf("_"));
                    if (keysArrayList.contains(fieldKey) && StringUtils.isNotBlank(fieldValue)) {
                        String fieldKeyId = valueField.optString(JsonFormConstants.KEY).substring(fieldKey.length() + 1);
                        valueField.put(JsonFormConstants.KEY, fieldKey);
                        HashMap<String, String> hashMap = repeatingGroupMap.get(fieldKeyId) == null ? new HashMap<>() : repeatingGroupMap.get(fieldKeyId);
                        hashMap.put(fieldKey, fieldValue);
                        repeatingGroupMap.put(fieldKeyId, hashMap);
                    }
                }
            }
        }
        return repeatingGroupMap;
    }

    @Nullable
    public static HashMap<String, String> getMaternityClient(@NonNull String baseEntityId) {
        ArrayList<HashMap<String, String>> hashMap = CoreLibrary.getInstance().context().getEventClientRepository().rawQuery(MaternityLibrary.getInstance().getRepository().getReadableDatabase(),
                "select * from " + metadata().getTableName() +
                        " where " + metadata().getTableName() + ".id = '" + baseEntityId + "' limit 1");
        if (!hashMap.isEmpty()) {
            return hashMap.get(0);
        }
        return null;
    }

    public static String getNextUniqueId() {
        UniqueIdRepository uniqueIdRepo = MaternityLibrary.getInstance().getUniqueIdRepository();
        return uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
    }

    public static void saveMaternityChild(@NonNull List<MaternityEventClient> maternityEventClients) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();
            for (MaternityEventClient eventClient : maternityEventClients) {
                try {
                    Client baseClient = eventClient.getClient();
                    Event baseEvent = eventClient.getEvent();
                    JSONObject clientJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseClient));
                    MaternityLibrary.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                    JSONObject eventJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseEvent));
                    MaternityLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
                    currentFormSubmissionIds.add(baseEvent.getFormSubmissionId());
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            MaternityLibrary.getInstance().getClientProcessorForJava().processClient(MaternityLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void setActionButtonStatus(Button button, CommonPersonObjectClient client) {
        String baseEntityId = client.getCaseId();

        button.setTag(R.id.BUTTON_TYPE, R.string.start_maternity);
        button.setText(R.string.start_maternity);
        button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.maternity_outcome_bg));

        HashMap<String, String> data = MaternityLibrary.getInstance().getMaternityRegistrationDetailsRepository().findByBaseEntityId(baseEntityId);
        if (data != null) {

            if (client.getColumnmaps().get(MaternityConstants.JSON_FORM_KEY.MMI_BASE_ENTITY_ID) != null) {
                button.setText(R.string.outcome);
                button.setTag(R.id.BUTTON_TYPE, R.string.outcome);
            }
        }

        MaternityOutcomeForm maternityOutcomeForm = MaternityLibrary.getInstance().getMaternityOutcomeFormRepository().findOne(new MaternityOutcomeForm(baseEntityId));
        if (maternityOutcomeForm != null) {
            button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.form_saved_btn_bg));
        }
    }
}

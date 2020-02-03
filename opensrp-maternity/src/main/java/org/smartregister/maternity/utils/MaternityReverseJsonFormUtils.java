package org.smartregister.maternity.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Photo;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.maternity.enums.LocationHierarchy;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MaternityReverseJsonFormUtils {

    @Nullable
    public static String prepareJsonEditMaternityRegistrationForm(@NonNull Map<String, String> detailsMap, @NonNull List<String> nonEditableFields, @NonNull Context context) {
        try {
            MaternityMetadata maternityMetadata = MaternityUtils.metadata();

            if (maternityMetadata != null) {
                JSONObject form = new FormUtils(context).getFormJson(maternityMetadata.getMaternityRegistrationFormName());
                Timber.d("Original Form %s", form);
                if (form != null) {
                    MaternityJsonFormUtils.addRegLocHierarchyQuestions(form, MaternityConstants.JSON_FORM_KEY.ADDRESS_WIDGET_KEY, LocationHierarchy.ENTIRE_TREE);
                    form.put(MaternityConstants.JSON_FORM_KEY.ENTITY_ID, detailsMap.get(MaternityConstants.KEY.BASE_ENTITY_ID));

                    form.put(MaternityConstants.JSON_FORM_KEY.ENCOUNTER_TYPE, maternityMetadata.getUpdateEventType());
                    form.put(MaternityJsonFormUtils.CURRENT_ZEIR_ID, Utils.getValue(detailsMap, MaternityConstants.KEY.OPENSRP_ID, true).replace("-", ""));

                    form.getJSONObject(MaternityJsonFormUtils.STEP1).put(MaternityConstants.JSON_FORM_KEY.FORM_TITLE, MaternityConstants.JSON_FORM_KEY.MATERNITY_EDIT_FORM_TITLE);

                    JSONObject metadata = form.getJSONObject(MaternityJsonFormUtils.METADATA);
                    metadata.put(MaternityJsonFormUtils.ENCOUNTER_LOCATION, MaternityUtils.getAllSharedPreferences().fetchCurrentLocality());
                    JSONObject stepOne = form.getJSONObject(MaternityJsonFormUtils.STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(MaternityJsonFormUtils.FIELDS);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        setFormFieldValues(detailsMap, nonEditableFields, jsonObject);
                    }
                    Timber.d("Final Form %s", form);
                    return form.toString();
                } else {
                    Timber.e("Form cannot be found");
                }
            } else {
                Timber.e(new Exception(), "Could not start MATERNITY Edit Registration Form because MaternityMetadata is null");
            }
        } catch (Exception e) {
            Timber.e(e, "MaternityJsonFormUtils --> getMetadataForEditForm");
        }
        return null;
    }

    private static void setFormFieldValues(@NonNull Map<String, String> maternityDetails, @NonNull List<String> nonEditableFields, @NonNull JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityConstants.KEY.PHOTO)) {
            reversePhoto(maternityDetails.get(MaternityConstants.KEY.BASE_ENTITY_ID), jsonObject);
        } else if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityConstants.JSON_FORM_KEY.DOB_UNKNOWN)) {
            reverseDobUnknown(maternityDetails, jsonObject);
        } else if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityConstants.JSON_FORM_KEY.AGE_ENTERED)) {
            reverseAge(Utils.getValue(maternityDetails, MaternityConstants.JSON_FORM_KEY.AGE, false), jsonObject);
        } else if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityConstants.JSON_FORM_KEY.DOB_ENTERED)) {
            reverseDobEntered(maternityDetails, jsonObject);
        } else if (jsonObject.getString(MaternityJsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(MaternityJsonFormUtils.PERSON_IDENTIFIER)) {
            if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityJsonFormUtils.OPENSRP_ID)) {
                jsonObject.put(MaternityJsonFormUtils.VALUE, maternityDetails.get(MaternityConstants.KEY.OPENSRP_ID));
            } else {
                jsonObject.put(MaternityJsonFormUtils.VALUE, Utils.getValue(maternityDetails, jsonObject.getString(MaternityJsonFormUtils.OPENMRS_ENTITY_ID)
                        .toLowerCase(), false).replace("-", ""));
            }
        } else if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityConstants.JSON_FORM_KEY.HOME_ADDRESS)) {
            reverseHomeAddress(jsonObject, maternityDetails.get(MaternityConstants.JSON_FORM_KEY.HOME_ADDRESS));
        } else if (jsonObject.getString(MaternityJsonFormUtils.KEY).equalsIgnoreCase(MaternityConstants.JSON_FORM_KEY.REMINDERS)) {
            reverseReminders(maternityDetails, jsonObject);
        } else {
            jsonObject.put(MaternityJsonFormUtils.VALUE, getMappedValue(jsonObject.getString(MaternityJsonFormUtils.OPENMRS_ENTITY_ID), maternityDetails));
        }
        jsonObject.put(MaternityJsonFormUtils.READ_ONLY, nonEditableFields.contains(jsonObject.getString(MaternityJsonFormUtils.KEY)));
    }

    private static void reverseReminders(@NonNull Map<String, String> maternityDetails, @NonNull JSONObject jsonObject) throws JSONException {
        if (Boolean.valueOf(maternityDetails.get(MaternityConstants.JSON_FORM_KEY.REMINDERS))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(MaternityConstants.FormValue.IS_ENROLLED_IN_MESSAGES);
            jsonObject.put(MaternityJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reversePhoto(@NonNull String baseEntityId, @NonNull JSONObject jsonObject) throws JSONException {
        try {
            Photo photo = ImageUtils.profilePhotoByClientID(baseEntityId, MaternityImageUtils.getProfileImageResourceIdentifier());
            if (StringUtils.isNotBlank(photo.getFilePath())) {
                jsonObject.put(MaternityJsonFormUtils.VALUE, photo.getFilePath());
            }
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    private static void reverseDobUnknown(@NonNull Map<String, String> maternityDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String value = Utils.getValue(maternityDetails, MaternityConstants.JSON_FORM_KEY.DOB_UNKNOWN, false);
        if (!value.isEmpty() && Boolean.valueOf(maternityDetails.get(MaternityConstants.JSON_FORM_KEY.DOB_UNKNOWN))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(MaternityConstants.FormValue.IS_DOB_UNKNOWN);
            jsonObject.put(MaternityJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reverseDobEntered(@NonNull Map<String, String> maternityDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String dateString = maternityDetails.get(MaternityConstants.JSON_FORM_KEY.DOB);
        Date date = Utils.dobStringToDate(dateString);
        if (StringUtils.isNotBlank(dateString) && date != null) {
            jsonObject.put(MaternityJsonFormUtils.VALUE, com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT.format(date));
        }
    }

    private static void reverseHomeAddress(@NonNull JSONObject jsonObject, @Nullable String entity) throws JSONException {
        List<String> entityHierarchy = null;
        if (entity != null) {
            if (entity.equalsIgnoreCase(MaternityConstants.FormValue.OTHER)) {
                entityHierarchy = new ArrayList<>();
                entityHierarchy.add(entity);
            } else {
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(entity);
                entityHierarchy = LocationHelper.getInstance().getOpenMrsLocationHierarchy(locationId, true);
            }
        }

        String facilityHierarchyString = AssetHandler.javaToJsonString(entityHierarchy, new TypeToken<List<String>>() {}.getType());
        if (StringUtils.isNotBlank(facilityHierarchyString)) {
            jsonObject.put(MaternityJsonFormUtils.VALUE, facilityHierarchyString);
        }
    }

    protected static String getMappedValue(@NonNull String key, @NonNull Map<String, String> maternityDetails) {
        String value = Utils.getValue(maternityDetails, key, false);
        return !TextUtils.isEmpty(value) ? value : Utils.getValue(maternityDetails, key.toLowerCase(), false);
    }

    private static void reverseAge(@NonNull String value, @NonNull JSONObject jsonObject) throws JSONException {
        jsonObject.put(MaternityJsonFormUtils.VALUE, value);
    }
}
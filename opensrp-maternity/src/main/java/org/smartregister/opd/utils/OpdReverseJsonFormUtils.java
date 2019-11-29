package org.smartregister.opd.utils;

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
import org.smartregister.opd.enums.LocationHierarchy;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class OpdReverseJsonFormUtils {

    @Nullable
    public static String prepareJsonEditOpdRegistrationForm(@NonNull Map<String, String> detailsMap, @NonNull List<String> nonEditableFields, @NonNull Context context) {
        try {
            OpdMetadata opdMetadata = OpdUtils.metadata();

            if (opdMetadata != null) {
                JSONObject form = new FormUtils(context).getFormJson(opdMetadata.getOpdRegistrationFormName());
                Timber.d("Original Form %s", form);
                if (form != null) {
                    OpdJsonFormUtils.addRegLocHierarchyQuestions(form, OpdConstants.JSON_FORM_KEY.ADDRESS_WIDGET_KEY, LocationHierarchy.ENTIRE_TREE);
                    form.put(OpdConstants.JSON_FORM_KEY.ENTITY_ID, detailsMap.get(OpdConstants.KEY.BASE_ENTITY_ID));

                    form.put(OpdConstants.JSON_FORM_KEY.ENCOUNTER_TYPE, opdMetadata.getUpdateEventType());
                    form.put(OpdJsonFormUtils.CURRENT_ZEIR_ID, Utils.getValue(detailsMap, OpdConstants.KEY.OPENSRP_ID, true).replace("-", ""));

                    form.getJSONObject(OpdJsonFormUtils.STEP1).put(OpdConstants.JSON_FORM_KEY.FORM_TITLE, OpdConstants.JSON_FORM_KEY.OPD_EDIT_FORM_TITLE);

                    JSONObject metadata = form.getJSONObject(OpdJsonFormUtils.METADATA);
                    metadata.put(OpdJsonFormUtils.ENCOUNTER_LOCATION, OpdUtils.getAllSharedPreferences().fetchCurrentLocality());
                    JSONObject stepOne = form.getJSONObject(OpdJsonFormUtils.STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);

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
                Timber.e(new Exception(), "Could not start OPD Edit Registration Form because OpdMetadata is null");
            }
        } catch (Exception e) {
            Timber.e(e, "OpdJsonFormUtils --> getMetadataForEditForm");
        }
        return null;
    }

    private static void setFormFieldValues(@NonNull Map<String, String> opdDetails, @NonNull List<String> nonEditableFields, @NonNull JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdConstants.KEY.PHOTO)) {
            reversePhoto(opdDetails.get(OpdConstants.KEY.BASE_ENTITY_ID), jsonObject);
        } else if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdConstants.JSON_FORM_KEY.DOB_UNKNOWN)) {
            reverseDobUnknown(opdDetails, jsonObject);
        } else if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdConstants.JSON_FORM_KEY.AGE_ENTERED)) {
            reverseAge(Utils.getValue(opdDetails, OpdConstants.JSON_FORM_KEY.AGE, false), jsonObject);
        } else if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdConstants.JSON_FORM_KEY.DOB_ENTERED)) {
            reverseDobEntered(opdDetails, jsonObject);
        } else if (jsonObject.getString(OpdJsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(OpdJsonFormUtils.PERSON_IDENTIFIER)) {
            if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdJsonFormUtils.OPENSRP_ID)) {
                jsonObject.put(OpdJsonFormUtils.VALUE, opdDetails.get(OpdConstants.KEY.OPENSRP_ID));
            } else {
                jsonObject.put(OpdJsonFormUtils.VALUE, Utils.getValue(opdDetails, jsonObject.getString(OpdJsonFormUtils.OPENMRS_ENTITY_ID)
                        .toLowerCase(), false).replace("-", ""));
            }
        } else if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdConstants.JSON_FORM_KEY.HOME_ADDRESS)) {
            reverseHomeAddress(jsonObject, opdDetails.get(OpdConstants.JSON_FORM_KEY.HOME_ADDRESS));
        } else if (jsonObject.getString(OpdJsonFormUtils.KEY).equalsIgnoreCase(OpdConstants.JSON_FORM_KEY.REMINDERS)) {
            reverseReminders(opdDetails, jsonObject);
        } else {
            jsonObject.put(OpdJsonFormUtils.VALUE, getMappedValue(jsonObject.getString(OpdJsonFormUtils.OPENMRS_ENTITY_ID), opdDetails));
        }
        jsonObject.put(OpdJsonFormUtils.READ_ONLY, nonEditableFields.contains(jsonObject.getString(OpdJsonFormUtils.KEY)));
    }

    private static void reverseReminders(@NonNull Map<String, String> opdDetails, @NonNull JSONObject jsonObject) throws JSONException {
        if (Boolean.valueOf(opdDetails.get(OpdConstants.JSON_FORM_KEY.REMINDERS))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OpdConstants.FormValue.IS_ENROLLED_IN_MESSAGES);
            jsonObject.put(OpdJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reversePhoto(@NonNull String baseEntityId, @NonNull JSONObject jsonObject) throws JSONException {
        try {
            Photo photo = ImageUtils.profilePhotoByClientID(baseEntityId, OpdImageUtils.getProfileImageResourceIdentifier());
            if (StringUtils.isNotBlank(photo.getFilePath())) {
                jsonObject.put(OpdJsonFormUtils.VALUE, photo.getFilePath());
            }
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    private static void reverseDobUnknown(@NonNull Map<String, String> opdDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String value = Utils.getValue(opdDetails, OpdConstants.JSON_FORM_KEY.DOB_UNKNOWN, false);
        if (!value.isEmpty() && Boolean.valueOf(opdDetails.get(OpdConstants.JSON_FORM_KEY.DOB_UNKNOWN))) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OpdConstants.FormValue.IS_DOB_UNKNOWN);
            jsonObject.put(OpdJsonFormUtils.VALUE, jsonArray);
        }
    }

    private static void reverseDobEntered(@NonNull Map<String, String> opdDetails, @NonNull JSONObject jsonObject) throws JSONException {
        String dateString = opdDetails.get(OpdConstants.JSON_FORM_KEY.DOB);
        Date date = Utils.dobStringToDate(dateString);
        if (StringUtils.isNotBlank(dateString) && date != null) {
            jsonObject.put(OpdJsonFormUtils.VALUE, com.vijay.jsonwizard.widgets.DatePickerFactory.DATE_FORMAT.format(date));
        }
    }

    private static void reverseHomeAddress(@NonNull JSONObject jsonObject, @Nullable String entity) throws JSONException {
        List<String> entityHierarchy = null;
        if (entity != null) {
            if (entity.equalsIgnoreCase(OpdConstants.FormValue.OTHER)) {
                entityHierarchy = new ArrayList<>();
                entityHierarchy.add(entity);
            } else {
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(entity);
                entityHierarchy = LocationHelper.getInstance().getOpenMrsLocationHierarchy(locationId, true);
            }
        }

        String facilityHierarchyString = AssetHandler.javaToJsonString(entityHierarchy, new TypeToken<List<String>>() {}.getType());
        if (StringUtils.isNotBlank(facilityHierarchyString)) {
            jsonObject.put(OpdJsonFormUtils.VALUE, facilityHierarchyString);
        }
    }

    protected static String getMappedValue(@NonNull String key, @NonNull Map<String, String> opdDetails) {
        String value = Utils.getValue(opdDetails, key, false);
        return !TextUtils.isEmpty(value) ? value : Utils.getValue(opdDetails, key.toLowerCase(), false);
    }

    private static void reverseAge(@NonNull String value, @NonNull JSONObject jsonObject) throws JSONException {
        jsonObject.put(OpdJsonFormUtils.VALUE, value);
    }
}
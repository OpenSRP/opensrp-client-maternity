package org.smartregister.maternity.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.maternity.utils.MaternityJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

public class MaternityOutcomeFormProcessingTask implements MaternityFormProcessingTask<List<Event>> {

    @Override
    public List<Event> processMaternityForm(@NonNull String jsonString, @Nullable Intent data) throws JSONException {
        return processMaternityOutcomeForm(jsonString, data);
    }

    private List<Event> processMaternityOutcomeForm(@NonNull String jsonString, @Nullable Intent data) throws JSONException {
        List<Event> eventList = new ArrayList<>();

        JSONObject jsonFormObject = new JSONObject(jsonString);

        String baseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);

        JSONArray fieldsArray = MaternityUtils.generateFieldsFromJsonForm(jsonFormObject);

        String steps = jsonFormObject.optString(JsonFormConstants.COUNT);

        int numOfSteps = Integer.parseInt(steps);

        for (int j = 0; j < numOfSteps; j++) {

            JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));

            String title = step.optString(JsonFormConstants.STEP_TITLE);

            if (MaternityConstants.JSON_FORM_STEP_NAME.BABIES_BORN.equals(title)) {

                HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn = MaternityUtils.buildRepeatingGroupValues(step, MaternityConstants.JSON_FORM_KEY.BABIES_BORN);

                if (!buildRepeatingGroupBorn.isEmpty()) {
                    if (StringUtils.isNotBlank(baseEntityId)) {

                        String[] ids = MaternityUtils.generateNIds(buildRepeatingGroupBorn.size());
                        int count = 0;
                        for (Map.Entry<String, HashMap<String, String>> entrySet : buildRepeatingGroupBorn.entrySet()) {
                            entrySet.getValue().put(MaternityDbConstants.Column.MaternityChild.BASE_ENTITY_ID, ids[count]);
                            count++;
                        }

                        createChildClients(jsonFormObject, baseEntityId, buildRepeatingGroupBorn);
                    }
                    String strGroup = gson.toJson(buildRepeatingGroupBorn);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, MaternityConstants.JSON_FORM_KEY.BABIES_BORN_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                }
            } else if (MaternityConstants.JSON_FORM_STEP_NAME.STILL_BORN_BABIES.equals(title)) {

                HashMap<String, HashMap<String, String>> buildRepeatingGroupStillBorn = MaternityUtils.buildRepeatingGroupValues(step, MaternityConstants.JSON_FORM_KEY.BABIES_STILLBORN);

                if (!buildRepeatingGroupStillBorn.isEmpty()) {
                    String strGroup = gson.toJson(buildRepeatingGroupStillBorn);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, MaternityConstants.JSON_FORM_KEY.BABIES_STILL_BORN_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                }
            }
        }

        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        Event maternityOutcomeEvent = MaternityJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_OUTCOME, "");
        MaternityJsonFormUtils.tagSyncMetadata(maternityOutcomeEvent);
        eventList.add(maternityOutcomeEvent);

        Event closeMaternityEvent = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_CLOSE, "");
        MaternityJsonFormUtils.tagSyncMetadata(closeMaternityEvent);
        closeMaternityEvent.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_END_DATE, MaternityUtils.convertDate(new Date(), MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeMaternityEvent);
        return eventList;
    }

    public void createChildClients(@NonNull JSONObject jsonFormObject, @NonNull String baseEntityId,
                                   @NonNull HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn) {
        MaternityLibrary.getInstance().getAppExecutors().diskIO().execute(() -> {
            List<MaternityEventClient> childEvents = buildChildRegistrationEvents(buildRepeatingGroupBorn, baseEntityId, jsonFormObject);
            if (!childEvents.isEmpty()) {
                saveMaternityChild(childEvents);
            }
        });
    }

    protected void saveMaternityChild(List<MaternityEventClient> childEvents) {
        MaternityUtils.saveMaternityChild(childEvents);
    }

    @NonNull
    private List<MaternityEventClient> buildChildRegistrationEvents(@NonNull HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn,
                                                                    @NonNull String baseEntityId,
                                                                    @NonNull JSONObject jsonFormObject) {
        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        String strBabiesBorn = gson.toJson(buildRepeatingGroupBorn);

        List<MaternityEventClient> childRegEventList = new ArrayList<>();

        if (StringUtils.isNotBlank(strBabiesBorn)) {

            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);

                Iterator<String> repeatingGroupKeys = jsonObject.keys();

                HashMap<String, String> motherDetails = motherDetails(baseEntityId);
                if (motherDetails != null) {
                    while (repeatingGroupKeys.hasNext()) {
                        JSONObject jsonChildObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                        String dischargedAlive = jsonChildObject.optString(MaternityConstants.JSON_FORM_KEY.DISCHARGED_ALIVE);
                        if (StringUtils.isNotBlank(dischargedAlive) && dischargedAlive.equalsIgnoreCase("yes")) {
                            String entityId = jsonChildObject.optString(MaternityDbConstants.Column.MaternityChild.BASE_ENTITY_ID);
                            JSONArray fields = populateChildFieldArray(jsonChildObject, motherDetails);
                            if (fields != null) {
                                Client baseClient = JsonFormUtils.createBaseClient(fields, formTag, entityId);
                                baseClient.addRelationship(MaternityConstants.MOTHER, baseEntityId);
                                baseClient.setRelationalBaseEntityId(baseEntityId);
                                Event childRegEvent = MaternityJsonFormUtils.createEvent(fields, jsonFormObject.optJSONObject(METADATA)
                                        , formTag, entityId, childRegistrationEvent(), "");
                                MaternityJsonFormUtils.tagSyncMetadata(childRegEvent);
                                childRegEventList.add(new MaternityEventClient(baseClient, childRegEvent));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        return childRegEventList;
    }

    @Nullable
    @VisibleForTesting
    public JSONArray getChildFormFields() {
        if (StringUtils.isNotBlank(getChildFormName())) {
            JSONObject formJsonObject = MaternityUtils.getFormUtils().getFormJson(getChildFormName());
            if (formJsonObject != null) {
                return FormUtils.getMultiStepFormFields(formJsonObject);
            }
        }
        return null;
    }

    @NonNull
    public String childRegistrationEvent() {
        return MaternityConstants.EventType.BIRTH_REGISTRATION;
    }

    @NonNull
    public String getChildFormName() {
        return "";
    }

    @Nullable
    private JSONArray populateChildFieldArray(@NonNull JSONObject maternityBabyBorn, @NonNull HashMap<String, String> motherDetails) throws JSONException {
        JSONArray jsonArray = getChildFormFields();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String key = jsonObject.optString(JsonFormConstants.KEY);
                String childKeyToKeyValue = childFormKeyToKeyMap().get(key);
                if (maternityBabyBorn.has(key)) {
                    jsonObject.put(JsonFormConstants.VALUE, maternityBabyBorn.optString(key));
                } else if (StringUtils.isNotBlank(childKeyToKeyValue) && maternityBabyBorn.has(childKeyToKeyValue)) {
                    jsonObject.put(JsonFormConstants.VALUE, maternityBabyBorn.optString(childKeyToKeyValue));
                } else if (key.equalsIgnoreCase(childOpensrpId())) {
                    jsonObject.put(JsonFormConstants.VALUE, MaternityUtils.getNextUniqueId());
                } else if (otherRequiredFields().contains(key)) {
                    jsonObject.put(JsonFormConstants.VALUE, motherDetails.get(childKeyToColumnMap().get(key) == null ? key : childKeyToColumnMap().get(key)));
                }
            }
            MaternityJsonFormUtils.lastInteractedWith(jsonArray);
            return jsonArray;
        }
        return null;
    }

    @NonNull
    public Set<String> otherRequiredFields() {
        return childKeyToColumnMap().keySet();
    }

    @NonNull
    public String childOpensrpId() {
        return MaternityConstants.JSON_FORM_KEY.ZEIR_ID;
    }

    @NonNull
    public HashMap<String, String> childFormKeyToKeyMap() {
        return new HashMap<>();
    }

    @NonNull
    public HashMap<String, String> childKeyToColumnMap() {
        return new HashMap<>();
    }

    @Nullable
    public HashMap<String, String> motherDetails(@NonNull String baseEntityId) {
        return MaternityUtils.getMaternityClient(baseEntityId);
    }
}

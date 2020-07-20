package org.smartregister.maternity.processor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.MaternityCloseEventProcessException;
import org.smartregister.maternity.pojo.MaternityChild;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private HashSet<String> eventTypes = null;

    public MaternityMiniClientProcessorForJava(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes == null) {
            eventTypes = new HashSet<>();
            eventTypes.add(MaternityConstants.EventType.MATERNITY_REGISTRATION);
            eventTypes.add(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_OUTCOME);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_MEDIC_INFO);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_CLOSE);
        }

        return eventTypes;
    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {
        Event event = eventClient.getEvent();

        String eventType = event.getEventType();

        if (eventType.equals(MaternityConstants.EventType.MATERNITY_REGISTRATION)
                || eventType.equals(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION)) {
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new MaternityCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), MaternityConstants.EventType.MATERNITY_CLOSE));
            }
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            unsyncEvents.add(event);
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_MEDIC_INFO)) {
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            processMedicInfo(eventClient);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_OUTCOME)) {
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            processMaternityOutcome(eventClient);
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        }
    }

    private void processMedicInfo(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();
        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        keyValues.put(MaternityConstants.JSON_FORM_KEY.MATERNITY_MEDIC_INFO_SUBMITTED, "1");
        keyValues.put(MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID, eventClient.getClient().getBaseEntityId());

        MaternityLibrary.getInstance().getMaternityRegistrationDetailsRepository().saveOrUpdate(MaternityUtils.convertMapToContentValues(refineForMedicInfo(keyValues)));
    }

    private void processMaternityOutcome(@NonNull EventClient eventClient) {
        Event event = eventClient.getEvent();
        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);
        String strStillBorn = keyValues.get(MaternityConstants.JSON_FORM_KEY.BABIES_STILL_BORN_MAP);
        processStillBorn(strStillBorn, event);
        String strBabiesBorn = keyValues.get(MaternityConstants.JSON_FORM_KEY.BABIES_BORN_MAP);
        processBabiesBorn(strBabiesBorn, event);
    }

    private HashMap<String, String> refineForMedicInfo(HashMap<String, String> rawMap) {
        HashMap<String, String> columns = new HashMap<>();
        columns.put(MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID, rawMap.get(MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID));
        columns.put(MaternityConstants.JSON_FORM_KEY.MATERNITY_MEDIC_INFO_SUBMITTED, rawMap.get(MaternityConstants.JSON_FORM_KEY.MATERNITY_MEDIC_INFO_SUBMITTED));
        columns.put("gravidity", rawMap.get("gravidity"));
        columns.put("parity", rawMap.get("parity"));
        columns.put("abortion_number", rawMap.get("abortion_number"));
        columns.put("lmp", rawMap.get("lmp"));
        columns.put("lmp_unknown", rawMap.get("lmp_unknown"));
        columns.put("gest_age", rawMap.get("gest_age"));
        columns.put("ga_weeks_entered", rawMap.get("ga_weeks_entered"));
        columns.put("ga_days_entered", rawMap.get("ga_days_entered"));
        columns.put("ga_calculated", rawMap.get("ga_calculated"));
        columns.put("onset_labour_date", rawMap.get("onset_labour_date"));
        columns.put("onset_labour_time", rawMap.get("onset_labour_time"));
        columns.put("previous_delivery_mode", rawMap.get("previous_delivery_mode"));
        columns.put("previous_pregnancy_outcomes", rawMap.get("previous_pregnancy_outcomes"));
        columns.put("previous_complications", rawMap.get("previous_complications"));
        columns.put("previous_complications_other", rawMap.get("previous_complications_other"));
        columns.put("surgeries", rawMap.get("surgeries"));
        columns.put("surgeries_other_gyn_proced", rawMap.get("surgeries_other_gyn_proced"));
        columns.put("surgeries_other", rawMap.get("surgeries_other"));
        columns.put("health_conditions", rawMap.get("health_conditions"));
        columns.put("health_conditions_other", rawMap.get("health_conditions_other"));
        columns.put("family_history", rawMap.get("family_history"));
        columns.put("family_history_other", rawMap.get("family_history_other"));
        return columns;
    }

    private void processBabiesBorn(@Nullable String strBabiesBorn, @NonNull Event event) {
        if (StringUtils.isNotBlank(strBabiesBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strBabiesBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonTestObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    MaternityChild maternityChild = new MaternityChild();
                    maternityChild.setMotherBaseEntityId(event.getBaseEntityId());
                    maternityChild.setApgar(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.APGAR));
                    maternityChild.setBfFirstHour(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BF_FIRST_HOUR));
                    maternityChild.setComplications(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_COMPLICATIONS));
                    maternityChild.setComplicationsOther(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_COMPLICATIONS_OTHER));
                    maternityChild.setCareMgt(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_CARE_MGT));
                    maternityChild.setFirstCry(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_FIRST_CRY));
                    maternityChild.setDischargedAlive(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.DISCHARGED_ALIVE));
                    maternityChild.setDob(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_DOB));
                    maternityChild.setFirstName(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_FIRST_NAME));
                    maternityChild.setLastName(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_LAST_NAME));
                    maternityChild.setGender(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_GENDER));
                    maternityChild.setChildHivStatus(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.CHILD_HIV_STATUS));
                    maternityChild.setHeight(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BIRTH_HEALTH_ENTERED));
                    maternityChild.setWeight(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BIRTH_WEIGHT_ENTERED));
                    maternityChild.setEventDate(MaternityUtils.convertDate(event.getEventDate().toDate(), MaternityDbConstants.DATE_FORMAT));
                    maternityChild.setNvpAdministration(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.NVP_ADMINISTRATION));
                    maternityChild.setInterventionSpecify(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_INTERVENTION_SPECIFY));
                    maternityChild.setInterventionReferralLocation(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.BABY_INTERVENTION_REFERRAL_LOCATION));
                    MaternityLibrary.getInstance().getMaternityChildRepository().saveOrUpdate(maternityChild);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void processStillBorn(@Nullable String strStillBorn, @NonNull Event event) {
        if (StringUtils.isNotBlank(strStillBorn)) {
            try {
                JSONObject jsonObject = new JSONObject(strStillBorn);
                Iterator<String> repeatingGroupKeys = jsonObject.keys();
                while (repeatingGroupKeys.hasNext()) {
                    JSONObject jsonTestObject = jsonObject.optJSONObject(repeatingGroupKeys.next());
                    MaternityChild maternityStillBorn = new MaternityChild();
                    maternityStillBorn.setMotherBaseEntityId(event.getBaseEntityId());
                    maternityStillBorn.setStillBirthCondition(jsonTestObject.optString(MaternityConstants.JSON_FORM_KEY.STILLBIRTH_CONDITION));
                    maternityStillBorn.setEventDate(MaternityUtils.convertDate(event.getEventDate().toDate(), MaternityDbConstants.DATE_FORMAT));
                    MaternityLibrary.getInstance().getMaternityChildRepository().saveOrUpdate(maternityStillBorn);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues, boolean appendOnNewline) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);
                value = value != null ? value.trim() : value;

                if (!TextUtils.isEmpty(value)) {
                    if (appendOnNewline && keyValues.containsKey(key)) {
                        String currentValue = keyValues.get(key);
                        keyValues.put(key, value + "\n" + currentValue);
                    } else {
                        keyValues.put(key, value);
                    }
                    continue;
                }
            }

            List<Object> values = observation.getValues();
            if (values.size() > 0) {
                String value = (String) values.get(0);
                value = value != null ? value.trim() : value;

                if (!TextUtils.isEmpty(value)) {
                    if (appendOnNewline && keyValues.containsKey(key)) {
                        String currentValue = keyValues.get(key);
                        keyValues.put(key, value + "\n" + currentValue);
                    } else {
                        keyValues.put(key, value);
                    }
                }
            }
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues) {
        generateKeyValuesFromEvent(event, keyValues, false);
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        // Do nothing for now
        /*if (events != null) {
            for (Event event : events) {
                if (MaternityConstants.EventType.MATERNITY_CLOSE.equals(event.getEventType())) {
                    // Delete the maternity details
                    // MaternityLibrary.getInstance().getMaternityOutcomeDetailsRepository().delete(event.getBaseEntityId());

                    // Delete the actual client in the maternity table OR REMOVE THE Maternity register type
                    //updateRegisterTypeColumn(event, null);
                }
            }
        }*/
        return true;
    }
}
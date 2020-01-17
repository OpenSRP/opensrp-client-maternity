package org.smartregister.maternity.processor;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import net.sqlcipher.database.SQLiteException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.sync.MiniClientProcessorForJava;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.CheckInEventProcessException;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.pojos.OpdDiagnosis;
import org.smartregister.maternity.pojos.OpdServiceDetail;
import org.smartregister.maternity.pojos.OpdTestConducted;
import org.smartregister.maternity.pojos.OpdTreatment;
import org.smartregister.maternity.pojos.OpdVisit;
import org.smartregister.maternity.repository.MaternityDetailsRepository;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private HashSet<String> eventTypes = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(MaternityDbConstants.DATE_FORMAT, Locale.US);

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
            eventTypes.add(MaternityConstants.EventType.CHECK_IN);
            eventTypes.add(MaternityConstants.EventType.TEST_CONDUCTED);
            eventTypes.add(MaternityConstants.EventType.DIAGNOSIS);
            eventTypes.add(MaternityConstants.EventType.SERVICE_DETAIL);
            eventTypes.add(MaternityConstants.EventType.TREATMENT);
            eventTypes.add(MaternityConstants.EventType.CLOSE_OPD_VISIT);
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
            ArrayList<EventClient> eventClients = new ArrayList<>();
            eventClients.add(eventClient);
            processClient(eventClients);

            for (Obs obs: event.getObs()) {
                if (obs.getFieldCode().equals("conception_date")) {
                    MaternityDetails maternityDetails = new MaternityDetails();
                    maternityDetails.setBaseEntityId(eventClient.getClient().getBaseEntityId());
                    maternityDetails.setConceptionDate((String) obs.getValues().get(0));
                    maternityDetails.setCreatedAt(new Date());

                    //TODO: Figure out how to reuse the already created repository
                    MaternityLibrary.getInstance().getMaternityDetailsRepository().saveOrUpdate(maternityDetails);
                }
            }
        } else if (eventType.equals(MaternityConstants.EventType.CHECK_IN)) {
            if (eventClient.getClient() == null) {
                throw new CheckInEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), MaternityConstants.EventType.CHECK_IN));
            }

            processCheckIn(event, eventClient.getClient());
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        } else if (eventType.equals(MaternityConstants.EventType.TEST_CONDUCTED)) {
            try {
                processTestConducted(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (eventType.equals(MaternityConstants.EventType.DIAGNOSIS)) {
            try {
                processDiagnosis(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (eventType.equals(MaternityConstants.EventType.TREATMENT)) {
            try {
                processTreatment(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (eventType.equals(MaternityConstants.EventType.SERVICE_DETAIL)) {
            try {
                processServiceDetail(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (eventType.equals(MaternityConstants.EventType.CLOSE_OPD_VISIT)) {
            try {
                processOpdCloseVisitEvent(event);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processOpdCloseVisitEvent(@NonNull Event event) {
        Map<String, String> mapDetails = event.getDetails();
        //update visit end date
        if (mapDetails != null) {
            MaternityDetails maternityDetails = new MaternityDetails(event.getBaseEntityId(), mapDetails.get(MaternityConstants.JSON_FORM_KEY.VISIT_ID));
            maternityDetails = MaternityLibrary.getInstance().getMaternityDetailsRepository().findOne(maternityDetails);
            if (maternityDetails != null) {
                maternityDetails.setCurrentVisitEndDate(MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, mapDetails.get(MaternityConstants.JSON_FORM_KEY.VISIT_END_DATE)));
                boolean result = MaternityLibrary.getInstance().getMaternityDetailsRepository().saveOrUpdate(maternityDetails);
                if (result) {
                    Timber.d("Opd processOpdCloseVisitEvent for %s saved", event.getBaseEntityId());
                    return;
                }
                Timber.e("Opd processOpdCloseVisitEvent for %s not saved", event.getBaseEntityId());
            } else {
                Timber.e("Opd Details for %s not found", mapDetails.toString());
            }
        } else {
            Timber.e("Opd Details for %s not found, event details is null", event.getBaseEntityId());
        }
    }

    private void processServiceDetail(@NonNull Event event) {
        Map<String, String> mapDetails = event.getDetails();
        String id = mapDetails.get(MaternityConstants.JSON_FORM_KEY.ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");
        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        String serviceFee = keyValues.get(MaternityConstants.JSON_FORM_KEY.SERVICE_FEE);

        if (!TextUtils.isEmpty(serviceFee)) {
            OpdServiceDetail opdServiceDetail = new OpdServiceDetail();
            opdServiceDetail.setId(valueIds[0]);
            opdServiceDetail.setBaseEntityId(event.getBaseEntityId());
            opdServiceDetail.setFee(serviceFee);
            opdServiceDetail.setCreatedAt(Utils.convertDateFormat(new DateTime()));
            opdServiceDetail.setUpdatedAt(Utils.convertDateFormat(new DateTime()));
            opdServiceDetail.setVisitId(mapDetails.get(MaternityConstants.JSON_FORM_KEY.VISIT_ID));
            opdServiceDetail.setDetails(mapDetails.toString());
            boolean result = MaternityLibrary.getInstance().getOpdServiceDetailRepository().saveOrUpdate(opdServiceDetail);
            if (result) {
                Timber.d("Opd processServiceDetail for %s saved", event.getBaseEntityId());
                return;
            }
            Timber.e("Opd processServiceDetail for %s not saved", event.getBaseEntityId());
        }
    }

    private void processTreatment(@NonNull Event event) throws JSONException {
        Map<String, String> mapDetails = event.getDetails();
        String id = mapDetails.get(MaternityConstants.JSON_FORM_KEY.ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");

        JSONArray valueJsonArray = null;
        if(mapDetails.containsKey(MaternityConstants.KEY.VALUE)) {
            String strValue = mapDetails.get(MaternityConstants.KEY.VALUE);
            if (!StringUtils.isBlank(strValue)) {
                valueJsonArray = new JSONArray(strValue);
            }
        }

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);
        String medicine = keyValues.get(MaternityConstants.JSON_FORM_KEY.MEDICINE);
        if (!TextUtils.isEmpty(medicine)) {
            for (int i = 0; i < valueIds.length; i++) {
                OpdTreatment opdTreatment = new OpdTreatment();
                opdTreatment.setId(valueIds[i]);
                opdTreatment.setBaseEntityId(event.getBaseEntityId());
                opdTreatment.setVisitId(mapDetails.get(MaternityConstants.JSON_FORM_KEY.VISIT_ID));
                opdTreatment.setCreatedAt(Utils.convertDateFormat(new DateTime()));
                opdTreatment.setUpdatedAt(Utils.convertDateFormat(new DateTime()));

                if(valueJsonArray != null) {
                    JSONObject valueJsonObject = valueJsonArray.optJSONObject(i);

                    JSONObject propertyJsonObject = valueJsonObject.optJSONObject(JsonFormConstants.MultiSelectUtils.PROPERTY);
                    JSONObject meta = propertyJsonObject.optJSONObject(MaternityConstants.JSON_FORM_KEY.META);
                    if (meta != null) {
                        opdTreatment.setDosage(meta.optString(MaternityConstants.JSON_FORM_KEY.DOSAGE));
                        opdTreatment.setDuration(meta.optString(MaternityConstants.JSON_FORM_KEY.DURATION));
                        opdTreatment.setNote(meta.optString(MaternityConstants.JSON_FORM_KEY.INFO));
                    }
                    opdTreatment.setMedicine(valueJsonObject.optString(JsonFormConstants.MultiSelectUtils.TEXT));
                    opdTreatment.setProperty(valueJsonArray.toString());
                }

                boolean result = MaternityLibrary.getInstance().getOpdTreatmentRepository().saveOrUpdate(opdTreatment);
                if (result) {
                    Timber.i("Opd processTreatment for %s saved", event.getBaseEntityId());
                    continue;
                }
                Timber.e("Opd processTreatment for %s not saved", event.getBaseEntityId());
            }
        }
    }

    private void processDiagnosis(@NonNull Event event) throws JSONException {
        Map<String, String> mapDetails = event.getDetails();
        String id = mapDetails.get(MaternityConstants.JSON_FORM_KEY.ID);
        String visitId = mapDetails.get(MaternityConstants.JSON_FORM_KEY.VISIT_ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");

        JSONArray valuesJsonArray = null;
        if(mapDetails.containsKey(MaternityConstants.KEY.VALUE)) {
            String strValue = mapDetails.get(MaternityConstants.KEY.VALUE);
            if (!StringUtils.isBlank(strValue)) {
                valuesJsonArray = new JSONArray(strValue);
            }
        }

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        String diagnosis = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSIS);
        String diagnosisType = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);

        if (!TextUtils.isEmpty(diagnosis)) {
            for (int i = 0; i < valueIds.length; i++) {
                OpdDiagnosis opdDiagnosis = new OpdDiagnosis();
                opdDiagnosis.setBaseEntityId(event.getBaseEntityId());
                opdDiagnosis.setDiagnosis(diagnosis);
                opdDiagnosis.setType(diagnosisType);
                opdDiagnosis.setCreatedAt(Utils.convertDateFormat(new DateTime()));
                opdDiagnosis.setUpdatedAt(Utils.convertDateFormat(new DateTime()));
                opdDiagnosis.setVisitId(visitId);
                opdDiagnosis.setId(valueIds[i]);

                if(valuesJsonArray != null) {
                    JSONObject valueJsonObject = valuesJsonArray.optJSONObject(i);
                    JSONObject propertyJsonObject = valueJsonObject.optJSONObject(JsonFormConstants.MultiSelectUtils.PROPERTY);
                    opdDiagnosis.setIcd10Code(propertyJsonObject.optString(MaternityConstants.JSON_FORM_KEY.ICD10));
                    opdDiagnosis.setCode(propertyJsonObject.optString(MaternityConstants.JSON_FORM_KEY.CODE));
                    opdDiagnosis.setDetails(propertyJsonObject.optString(MaternityConstants.JSON_FORM_KEY.META));
                    opdDiagnosis.setDisease(valueJsonObject.optString(JsonFormConstants.MultiSelectUtils.TEXT));
                }


                boolean result = MaternityLibrary.getInstance().getOpdDiagnosisRepository().saveOrUpdate(opdDiagnosis);
                if (result) {
                    Timber.i("Opd processDiagnosis for %s saved", event.getBaseEntityId());
                    continue;
                }
                Timber.e("Opd processDiagnosis for %s not saved", event.getBaseEntityId());
            }
        }
    }

    private void processTestConducted(@NonNull Event event) {
        Map<String, String> mapDetails = event.getDetails();

        String id = mapDetails.get(MaternityConstants.JSON_FORM_KEY.ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        String diagnosticTest = extractDiagnosticTest(keyValues);
        String diagnosticResult = extractDiagnosticResult(keyValues);

        if (!TextUtils.isEmpty(diagnosticResult) && !TextUtils.isEmpty(diagnosticTest)) {
            OpdTestConducted opdTestConducted = new OpdTestConducted();
            opdTestConducted.setResult(diagnosticResult);
            opdTestConducted.setTest(diagnosticTest);
            opdTestConducted.setVisitId(mapDetails.get(MaternityConstants.JSON_FORM_KEY.VISIT_ID));
            opdTestConducted.setBaseEntityId(event.getBaseEntityId());
            opdTestConducted.setId(valueIds[0]);
            opdTestConducted.setCreatedAt(Utils.convertDateFormat(new DateTime()));
            opdTestConducted.setUpdatedAt(Utils.convertDateFormat(new DateTime()));

            boolean result = MaternityLibrary.getInstance().getOpdTestConductedRepository().saveOrUpdate(opdTestConducted);

            if (result) {
                Timber.i("Opd processTestConducted for %s saved", event.getBaseEntityId());
                return;
            }

            Timber.e("Opd processTestConducted for %s not saved", event.getBaseEntityId());
        }
    }

    @Nullable
    private String extractDiagnosticResult(@NonNull HashMap<String, String> keyValues) {
        String diagnosticResult = null;
        if (keyValues.containsKey(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER)) {
            diagnosticResult = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER);
        }

        if (keyValues.containsKey(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPECIFY)) {
            diagnosticResult = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPECIFY);
        }

        if (keyValues.containsKey(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER_BLOOD_TYPE)) {
            diagnosticResult = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER_BLOOD_TYPE);
        }

        if (keyValues.containsKey(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_GLUCOSE)) {
            diagnosticResult = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_GLUCOSE);
        }

        return diagnosticResult;
    }

    @Nullable
    private String extractDiagnosticTest(@NonNull HashMap<String, String> keyValues) {
        String diagnosticTest = null;
        if (keyValues.containsKey(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_OTHER)) {
            diagnosticTest = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_OTHER);
        }

        if (keyValues.containsKey(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST)) {
            diagnosticTest = keyValues.get(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST);
        }

        return diagnosticTest;
    }

    protected void processCheckIn(@NonNull Event event, @NonNull Client client) throws CheckInEventProcessException {
        HashMap<String, String> keyValues = new HashMap<>();

        // Todo: This might not work as expected when openmrs_entity_ids are added
        generateKeyValuesFromEvent(event, keyValues);

        Map<String, String> eventDetailsMap = event.getDetails();

        String visitId = eventDetailsMap.get(MaternityConstants.Event.CheckIn.Detail.VISIT_ID);
        String visitDateString = eventDetailsMap.get(MaternityConstants.Event.CheckIn.Detail.VISIT_DATE);

        Date visitDate = null;

        try {
            visitDate = dateFormat.parse(visitDateString != null ? visitDateString : "");
        } catch (ParseException e) {
            Timber.e(e);

            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null && visitId != null) {
            // Start transaction
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();

            boolean saved = saveVisit(event.getBaseEntityId(), event.getLocationId(), event.getProviderId(), visitId, visitDate);
            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("Visit with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            OpdCheckIn checkIn = generateCheckInRecordFromCheckInEvent(event, client, keyValues, visitId, visitDate);
            saved = MaternityLibrary.getInstance().getCheckInRepository().addCheckIn(checkIn);

            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("CheckIn for visit with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            //TODO: Make sure this does not override opd details which are latest

            // Update the detail
            MaternityDetails maternityDetails = generateOpdDetailsFromCheckInEvent(event, visitId, visitDate);
            saved = MaternityLibrary.getInstance().getMaternityDetailsRepository().saveOrUpdate(maternityDetails);

            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("OPD Details for visit with id %s updating status of client %s could not be saved in the db. Fail operation failed", visitId, event.getBaseEntityId()));
            }

            // Update the last interacted with of the user
            try {
                updateLastInteractedWith(event, visitId);
            } catch (SQLiteException ex) {
                abortTransaction();
                throw new CheckInEventProcessException("An error occurred saving last_interacted_with");
            }

            commitSuccessfulTransaction();
        } else {
            throw new CheckInEventProcessException(String.format("Check-in of event %s could not be processed because it the visitDate OR visitId is null", new Gson().toJson(event)));
        }
    }

    private boolean saveVisit(@NonNull String baseEntityId, @NonNull String locationId, @NonNull String providerId, @NonNull String visitId, @NonNull Date visitDate) {
        OpdVisit visit = new OpdVisit();

        visit.setId(visitId);
        visit.setBaseEntityId(baseEntityId);
        visit.setLocationId(locationId);
        visit.setProviderId(providerId);
        visit.setCreatedAt(new Date());
        visit.setVisitDate(visitDate);

        return MaternityLibrary.getInstance().getVisitRepository().addVisit(visit);
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();
            List<Object> values = observation.getValues();

            if (values.size() > 0) {
                String value = (String) values.get(0);

                if (!TextUtils.isEmpty(value)) {
                    keyValues.put(key, value);
                    continue;
                }
            }

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);

                if (!TextUtils.isEmpty(value)) {
                    keyValues.put(key, value);
                    continue;
                }
            }
        }
    }

    private void updateLastInteractedWith(@NonNull Event event, @NonNull String visitId) throws CheckInEventProcessException {
        String tableName = event.getEntityType();
        String lastInteractedWithDate = String.valueOf(new Date().getTime());

        ContentValues contentValues = new ContentValues();
        contentValues.put("last_interacted_with", lastInteractedWithDate);

        int recordsUpdated = MaternityLibrary.getInstance().getRepository().getWritableDatabase()
                .update(tableName, contentValues, "base_entity_id = ?", new String[]{event.getBaseEntityId()});

        if (recordsUpdated < 1) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }

        // Update FTS
        CommonRepository commonrepository = CoreLibrary.getInstance().context().commonrepository(tableName);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("last_interacted_with", lastInteractedWithDate);

        boolean isUpdated = false;
        String fieldName = "base_entity_id";
        if ("ec_child".equals(tableName)) {
            fieldName = "object_id";
        }

        if (commonrepository.isFts()) {
            recordsUpdated = MaternityLibrary.getInstance().getRepository().getWritableDatabase()
                    .update(CommonFtsObject.searchTableName(tableName), contentValues, fieldName + " = ?", new String[]{event.getBaseEntityId()});
            isUpdated = recordsUpdated > 0;
        }

        if (!isUpdated) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }
    }

    private void abortTransaction() {
        if (MaternityLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    private void commitSuccessfulTransaction() {
        if (MaternityLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().setTransactionSuccessful();
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    @NonNull
    private MaternityDetails generateOpdDetailsFromCheckInEvent(@NonNull Event event, String visitId, Date visitDate) {
        MaternityDetails maternityDetails = new MaternityDetails();
        maternityDetails.setBaseEntityId(event.getBaseEntityId());
        maternityDetails.setCurrentVisitId(visitId);
        maternityDetails.setCurrentVisitStartDate(visitDate);
        maternityDetails.setCurrentVisitEndDate(null);
        maternityDetails.setCreatedAt(new Date());

        // This code and flag is useless now - Todo: Work on disabling the flag in the query by deleting the current_visit_date & change this flag to diagnose_and_treat_ongoing
        // Set Pending diagnose and treat if we have not lapsed the max check-in duration in minutes set in the opd library configuration
        if (visitDate != null) {
            long timeDifferenceInMinutes = ((new Date().getTime()) - visitDate.getTime()) / (60 * 1000);
            maternityDetails.setPendingDiagnoseAndTreat(timeDifferenceInMinutes <= MaternityLibrary.getInstance().getMaternityConfiguration().getMaxCheckInDurationInMinutes());
        }

        return maternityDetails;
    }

    @NonNull
    private OpdCheckIn generateCheckInRecordFromCheckInEvent(@NonNull Event event, @NonNull Client client, HashMap<String, String> keyValues, String visitId, Date visitDate) {
        OpdCheckIn checkIn = new OpdCheckIn();
        checkIn.setVisitId(visitId);
        checkIn.setPregnancyStatus(keyValues.get(MaternityConstants.JsonFormField.PREGNANCY_STATUS));
        checkIn.setHasHivTestPreviously(keyValues.get(MaternityConstants.JsonFormField.HIV_TESTED));
        checkIn.setHivResultsPreviously(keyValues.get(MaternityConstants.JsonFormField.HIV_PREVIOUS_STATUS));
        checkIn.setIsTakingArt(keyValues.get(MaternityConstants.JsonFormField.IS_PATIENT_TAKING_ART));
        checkIn.setCurrentHivResult(keyValues.get(MaternityConstants.JsonFormField.CURRENT_HIV_STATUS));
        checkIn.setVisitType(keyValues.get(MaternityConstants.JsonFormField.VISIT_TYPE));
        checkIn.setAppointmentScheduledPreviously(keyValues.get(MaternityConstants.JsonFormField.APPOINTMENT_DUE));
        checkIn.setAppointmentDueDate(keyValues.get(MaternityConstants.JsonFormField.APPOINTMENT_DUE_DATE));
        checkIn.setFormSubmissionId(event.getFormSubmissionId());
        checkIn.setBaseEntityId(client.getBaseEntityId());
        checkIn.setUpdatedAt(new Date().getTime());

        if (visitDate != null) {
            checkIn.setCreatedAt(visitDate.getTime());
        }

        return checkIn;
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        return false;
    }
}
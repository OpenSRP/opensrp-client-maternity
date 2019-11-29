package org.smartregister.opd.processor;

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
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.exception.CheckInEventProcessException;
import org.smartregister.opd.pojos.OpdCheckIn;
import org.smartregister.opd.pojos.OpdDetails;
import org.smartregister.opd.pojos.OpdDiagnosis;
import org.smartregister.opd.pojos.OpdServiceDetail;
import org.smartregister.opd.pojos.OpdTestConducted;
import org.smartregister.opd.pojos.OpdTreatment;
import org.smartregister.opd.pojos.OpdVisit;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-01
 */

public class OpdMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private static OpdMiniClientProcessorForJava instance;

    private HashSet<String> eventTypes = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US);

    public OpdMiniClientProcessorForJava(Context context) {
        super(context);
    }

    public static OpdMiniClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new OpdMiniClientProcessorForJava(context);
        }

        return instance;
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes == null) {
            eventTypes = new HashSet<>();
            eventTypes.add(OpdConstants.EventType.CHECK_IN);
            eventTypes.add(OpdConstants.EventType.TEST_CONDUCTED);
            eventTypes.add(OpdConstants.EventType.DIAGNOSIS);
            eventTypes.add(OpdConstants.EventType.SERVICE_DETAIL);
            eventTypes.add(OpdConstants.EventType.TREATMENT);
            eventTypes.add(OpdConstants.EventType.CLOSE_OPD_VISIT);
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

        if (event.getEventType().equals(OpdConstants.EventType.CHECK_IN)) {
            if (eventClient.getClient() == null) {
                throw new CheckInEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), OpdConstants.EventType.CHECK_IN));
            }

            processCheckIn(event, eventClient.getClient());
            CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
        } else if (event.getEventType().equals(OpdConstants.EventType.TEST_CONDUCTED)) {
            try {
                processTestConducted(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (event.getEventType().equals(OpdConstants.EventType.DIAGNOSIS)) {
            try {
                processDiagnosis(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (event.getEventType().equals(OpdConstants.EventType.TREATMENT)) {
            try {
                processTreatment(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (event.getEventType().equals(OpdConstants.EventType.SERVICE_DETAIL)) {
            try {
                processServiceDetail(event);
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else if (event.getEventType().equals(OpdConstants.EventType.CLOSE_OPD_VISIT)) {
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
            OpdDetails opdDetails = new OpdDetails(event.getBaseEntityId(), mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_ID));
            opdDetails = OpdLibrary.getInstance().getOpdDetailsRepository().findOne(opdDetails);
            if (opdDetails != null) {
                opdDetails.setCurrentVisitEndDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE)));
                boolean result = OpdLibrary.getInstance().getOpdDetailsRepository().saveOrUpdate(opdDetails);
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
        String id = mapDetails.get(OpdConstants.JSON_FORM_KEY.ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");
        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        String serviceFee = keyValues.get(OpdConstants.JSON_FORM_KEY.SERVICE_FEE);

        if (!TextUtils.isEmpty(serviceFee)) {
            OpdServiceDetail opdServiceDetail = new OpdServiceDetail();
            opdServiceDetail.setId(valueIds[0]);
            opdServiceDetail.setBaseEntityId(event.getBaseEntityId());
            opdServiceDetail.setFee(serviceFee);
            opdServiceDetail.setCreatedAt(Utils.convertDateFormat(new DateTime()));
            opdServiceDetail.setUpdatedAt(Utils.convertDateFormat(new DateTime()));
            opdServiceDetail.setVisitId(mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_ID));
            opdServiceDetail.setDetails(mapDetails.toString());
            boolean result = OpdLibrary.getInstance().getOpdServiceDetailRepository().saveOrUpdate(opdServiceDetail);
            if (result) {
                Timber.d("Opd processServiceDetail for %s saved", event.getBaseEntityId());
                return;
            }
            Timber.e("Opd processServiceDetail for %s not saved", event.getBaseEntityId());
        }
    }

    private void processTreatment(@NonNull Event event) throws JSONException {
        Map<String, String> mapDetails = event.getDetails();
        String id = mapDetails.get(OpdConstants.JSON_FORM_KEY.ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");

        JSONArray valueJsonArray = null;
        if(mapDetails.containsKey(OpdConstants.KEY.VALUE)) {
            String strValue = mapDetails.get(OpdConstants.KEY.VALUE);
            if (!StringUtils.isBlank(strValue)) {
                valueJsonArray = new JSONArray(strValue);
            }
        }

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);
        String medicine = keyValues.get(OpdConstants.JSON_FORM_KEY.MEDICINE);
        if (!TextUtils.isEmpty(medicine)) {
            for (int i = 0; i < valueIds.length; i++) {
                OpdTreatment opdTreatment = new OpdTreatment();
                opdTreatment.setId(valueIds[i]);
                opdTreatment.setBaseEntityId(event.getBaseEntityId());
                opdTreatment.setVisitId(mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_ID));
                opdTreatment.setCreatedAt(Utils.convertDateFormat(new DateTime()));
                opdTreatment.setUpdatedAt(Utils.convertDateFormat(new DateTime()));

                if(valueJsonArray != null) {
                    JSONObject valueJsonObject = valueJsonArray.optJSONObject(i);

                    JSONObject propertyJsonObject = valueJsonObject.optJSONObject(JsonFormConstants.MultiSelectUtils.PROPERTY);
                    JSONObject meta = propertyJsonObject.optJSONObject(OpdConstants.JSON_FORM_KEY.META);
                    if (meta != null) {
                        opdTreatment.setDosage(meta.optString(OpdConstants.JSON_FORM_KEY.DOSAGE));
                        opdTreatment.setDuration(meta.optString(OpdConstants.JSON_FORM_KEY.DURATION));
                        opdTreatment.setNote(meta.optString(OpdConstants.JSON_FORM_KEY.INFO));
                    }
                    opdTreatment.setMedicine(valueJsonObject.optString(JsonFormConstants.MultiSelectUtils.TEXT));
                    opdTreatment.setProperty(valueJsonArray.toString());
                }

                boolean result = OpdLibrary.getInstance().getOpdTreatmentRepository().saveOrUpdate(opdTreatment);
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
        String id = mapDetails.get(OpdConstants.JSON_FORM_KEY.ID);
        String visitId = mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_ID);
        if (id == null) {
            return;
        }
        String[] valueIds = id.split(",");

        JSONArray valuesJsonArray = null;
        if(mapDetails.containsKey(OpdConstants.KEY.VALUE)) {
            String strValue = mapDetails.get(OpdConstants.KEY.VALUE);
            if (!StringUtils.isBlank(strValue)) {
                valuesJsonArray = new JSONArray(strValue);
            }
        }

        HashMap<String, String> keyValues = new HashMap<>();
        generateKeyValuesFromEvent(event, keyValues);

        String diagnosis = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSIS);
        String diagnosisType = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);

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
                    opdDiagnosis.setIcd10Code(propertyJsonObject.optString(OpdConstants.JSON_FORM_KEY.ICD10));
                    opdDiagnosis.setCode(propertyJsonObject.optString(OpdConstants.JSON_FORM_KEY.CODE));
                    opdDiagnosis.setDetails(propertyJsonObject.optString(OpdConstants.JSON_FORM_KEY.META));
                    opdDiagnosis.setDisease(valueJsonObject.optString(JsonFormConstants.MultiSelectUtils.TEXT));
                }


                boolean result = OpdLibrary.getInstance().getOpdDiagnosisRepository().saveOrUpdate(opdDiagnosis);
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

        String id = mapDetails.get(OpdConstants.JSON_FORM_KEY.ID);
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
            opdTestConducted.setVisitId(mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_ID));
            opdTestConducted.setBaseEntityId(event.getBaseEntityId());
            opdTestConducted.setId(valueIds[0]);
            opdTestConducted.setCreatedAt(Utils.convertDateFormat(new DateTime()));
            opdTestConducted.setUpdatedAt(Utils.convertDateFormat(new DateTime()));

            boolean result = OpdLibrary.getInstance().getOpdTestConductedRepository().saveOrUpdate(opdTestConducted);

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
        if (keyValues.containsKey(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER)) {
            diagnosticResult = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER);
        }

        if (keyValues.containsKey(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPECIFY)) {
            diagnosticResult = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPECIFY);
        }

        if (keyValues.containsKey(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER_BLOOD_TYPE)) {
            diagnosticResult = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER_BLOOD_TYPE);
        }

        if (keyValues.containsKey(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_GLUCOSE)) {
            diagnosticResult = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_GLUCOSE);
        }

        return diagnosticResult;
    }

    @Nullable
    private String extractDiagnosticTest(@NonNull HashMap<String, String> keyValues) {
        String diagnosticTest = null;
        if (keyValues.containsKey(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_OTHER)) {
            diagnosticTest = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_OTHER);
        }

        if (keyValues.containsKey(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST)) {
            diagnosticTest = keyValues.get(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST);
        }

        return diagnosticTest;
    }

    protected void processCheckIn(@NonNull Event event, @NonNull Client client) throws CheckInEventProcessException {
        HashMap<String, String> keyValues = new HashMap<>();

        // Todo: This might not work as expected when openmrs_entity_ids are added
        generateKeyValuesFromEvent(event, keyValues);

        Map<String, String> eventDetailsMap = event.getDetails();

        String visitId = eventDetailsMap.get(OpdConstants.Event.CheckIn.Detail.VISIT_ID);
        String visitDateString = eventDetailsMap.get(OpdConstants.Event.CheckIn.Detail.VISIT_DATE);

        Date visitDate = null;

        try {
            visitDate = dateFormat.parse(visitDateString != null ? visitDateString : "");
        } catch (ParseException e) {
            Timber.e(e);

            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null && visitId != null) {
            // Start transaction
            OpdLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();

            boolean saved = saveVisit(event.getBaseEntityId(), event.getLocationId(), event.getProviderId(), visitId, visitDate);
            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("Visit with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            OpdCheckIn checkIn = generateCheckInRecordFromCheckInEvent(event, client, keyValues, visitId, visitDate);
            saved = OpdLibrary.getInstance().getCheckInRepository().addCheckIn(checkIn);

            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("CheckIn for visit with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            //TODO: Make sure this does not override opd details which are latest

            // Update the detail
            OpdDetails opdDetails = generateOpdDetailsFromCheckInEvent(event, visitId, visitDate);
            saved = OpdLibrary.getInstance().getOpdDetailsRepository().saveOrUpdate(opdDetails);

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

        return OpdLibrary.getInstance().getVisitRepository().addVisit(visit);
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

        int recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
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
            recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
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
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    private void commitSuccessfulTransaction() {
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().setTransactionSuccessful();
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    @NonNull
    private OpdDetails generateOpdDetailsFromCheckInEvent(@NonNull Event event, String visitId, Date visitDate) {
        OpdDetails opdDetails = new OpdDetails();
        opdDetails.setBaseEntityId(event.getBaseEntityId());
        opdDetails.setCurrentVisitId(visitId);
        opdDetails.setCurrentVisitStartDate(visitDate);
        opdDetails.setCurrentVisitEndDate(null);
        opdDetails.setCreatedAt(new Date());

        // This code and flag is useless now - Todo: Work on disabling the flag in the query by deleting the current_visit_date & change this flag to diagnose_and_treat_ongoing
        // Set Pending diagnose and treat if we have not lapsed the max check-in duration in minutes set in the opd library configuration
        if (visitDate != null) {
            long timeDifferenceInMinutes = ((new Date().getTime()) - visitDate.getTime()) / (60 * 1000);
            opdDetails.setPendingDiagnoseAndTreat(timeDifferenceInMinutes <= OpdLibrary.getInstance().getOpdConfiguration().getMaxCheckInDurationInMinutes());
        }

        return opdDetails;
    }

    @NonNull
    private OpdCheckIn generateCheckInRecordFromCheckInEvent(@NonNull Event event, @NonNull Client client, HashMap<String, String> keyValues, String visitId, Date visitDate) {
        OpdCheckIn checkIn = new OpdCheckIn();
        checkIn.setVisitId(visitId);
        checkIn.setPregnancyStatus(keyValues.get(OpdConstants.JsonFormField.PREGNANCY_STATUS));
        checkIn.setHasHivTestPreviously(keyValues.get(OpdConstants.JsonFormField.HIV_TESTED));
        checkIn.setHivResultsPreviously(keyValues.get(OpdConstants.JsonFormField.HIV_PREVIOUS_STATUS));
        checkIn.setIsTakingArt(keyValues.get(OpdConstants.JsonFormField.IS_PATIENT_TAKING_ART));
        checkIn.setCurrentHivResult(keyValues.get(OpdConstants.JsonFormField.CURRENT_HIV_STATUS));
        checkIn.setVisitType(keyValues.get(OpdConstants.JsonFormField.VISIT_TYPE));
        checkIn.setAppointmentScheduledPreviously(keyValues.get(OpdConstants.JsonFormField.APPOINTMENT_DUE));
        checkIn.setAppointmentDueDate(keyValues.get(OpdConstants.JsonFormField.APPOINTMENT_DUE_DATE));
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
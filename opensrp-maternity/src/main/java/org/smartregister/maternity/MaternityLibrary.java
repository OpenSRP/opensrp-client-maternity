package org.smartregister.maternity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.domain.YamlConfig;
import org.smartregister.maternity.domain.YamlConfigItem;
import org.smartregister.maternity.helper.MaternityRulesEngineHelper;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.maternity.pojos.OpdVisit;
import org.smartregister.maternity.repository.MaternityDetailsRepository;
import org.smartregister.maternity.repository.OpdCheckInRepository;
import org.smartregister.maternity.repository.OpdDiagnosisAndTreatmentFormRepository;
import org.smartregister.maternity.repository.OpdDiagnosisRepository;
import org.smartregister.maternity.repository.OpdServiceDetailRepository;
import org.smartregister.maternity.repository.OpdTestConductedRepository;
import org.smartregister.maternity.repository.OpdTreatmentRepository;
import org.smartregister.maternity.repository.OpdVisitRepository;
import org.smartregister.maternity.repository.OpdVisitSummaryRepository;
import org.smartregister.maternity.utils.FilePath;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;
import timber.log.Timber;

import static org.smartregister.maternity.utils.MaternityJsonFormUtils.METADATA;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityLibrary {

    private static MaternityLibrary instance;
    private final Context context;
    private final Repository repository;
    private MaternityConfiguration maternityConfiguration;
    private ECSyncHelper syncHelper;

    private UniqueIdRepository uniqueIdRepository;
    private OpdCheckInRepository checkInRepository;
    private OpdVisitRepository visitRepository;
    private MaternityDetailsRepository maternityDetailsRepository;
    private OpdDiagnosisAndTreatmentFormRepository opdDiagnosisAndTreatmentFormRepository;
    private OpdServiceDetailRepository opdServiceDetailRepository;
    private OpdDiagnosisRepository opdDiagnosisRepository;
    private OpdTreatmentRepository opdTreatmentRepository;
    private OpdTestConductedRepository opdTestConductedRepository;
    private OpdVisitSummaryRepository opdVisitSummaryRepository;

    private Compressor compressor;
    private int applicationVersion;
    private int databaseVersion;

    private Yaml yaml;

    private MaternityRulesEngineHelper maternityRulesEngineHelper;

    protected MaternityLibrary(@NonNull Context context, @NonNull MaternityConfiguration maternityConfiguration
            , @NonNull Repository repository, int applicationVersion, int databaseVersion) {
        this.context = context;
        this.maternityConfiguration = maternityConfiguration;
        this.repository = repository;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;

        // Initialize configs processor
        initializeYamlConfigs();
    }

    public static void init(Context context, @NonNull Repository repository, @NonNull MaternityConfiguration maternityConfiguration
            , int applicationVersion, int databaseVersion) {
        if (instance == null) {
            instance = new MaternityLibrary(context, maternityConfiguration, repository, applicationVersion, databaseVersion);
        }
    }

    public static MaternityLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance does not exist!!! Call "
                    + MaternityLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class");
        }
        return instance;
    }

    @NonNull
    public Context context() {
        return context;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository(getRepository());
        }
        return uniqueIdRepository;
    }

    @NonNull
    public OpdCheckInRepository getCheckInRepository() {
        if (checkInRepository == null) {
            checkInRepository = new OpdCheckInRepository(getRepository());
        }

        return checkInRepository;
    }

    @NonNull
    public OpdVisitRepository getVisitRepository() {
        if (visitRepository == null) {
            visitRepository = new OpdVisitRepository(getRepository());
        }

        return visitRepository;
    }

    @NonNull
    public MaternityDetailsRepository getMaternityDetailsRepository() {
        if (maternityDetailsRepository == null) {
            maternityDetailsRepository = new MaternityDetailsRepository(getRepository());
        }
        return maternityDetailsRepository;
    }

    @NonNull
    public OpdDiagnosisAndTreatmentFormRepository getOpdDiagnosisAndTreatmentFormRepository() {
        if (opdDiagnosisAndTreatmentFormRepository == null) {
            opdDiagnosisAndTreatmentFormRepository = new OpdDiagnosisAndTreatmentFormRepository(getRepository());
        }
        return opdDiagnosisAndTreatmentFormRepository;
    }

    @NonNull
    public OpdDiagnosisRepository getOpdDiagnosisRepository() {
        if (opdDiagnosisRepository == null) {
            opdDiagnosisRepository = new OpdDiagnosisRepository(getRepository());
        }
        return opdDiagnosisRepository;
    }

    @NonNull
    public OpdTestConductedRepository getOpdTestConductedRepository() {
        if (opdTestConductedRepository == null) {
            opdTestConductedRepository = new OpdTestConductedRepository(getRepository());
        }
        return opdTestConductedRepository;
    }

    @NonNull
    public OpdTreatmentRepository getOpdTreatmentRepository() {
        if (opdTreatmentRepository == null) {
            opdTreatmentRepository = new OpdTreatmentRepository(getRepository());
        }
        return opdTreatmentRepository;
    }

    @NonNull
    public OpdServiceDetailRepository getOpdServiceDetailRepository() {
        if (opdServiceDetailRepository == null) {
            opdServiceDetailRepository = new OpdServiceDetailRepository(getRepository());
        }
        return opdServiceDetailRepository;
    }

    @NonNull
    public OpdVisitSummaryRepository getOpdVisitSummaryRepository() {
        if (opdVisitSummaryRepository == null) {
            opdVisitSummaryRepository = new OpdVisitSummaryRepository(getRepository());
        }
        return opdVisitSummaryRepository;
    }

    @NonNull
    public Repository getRepository() {
        return repository;
    }


    @NonNull
    public ECSyncHelper getEcSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(context().applicationContext());
        }
        return syncHelper;
    }

    @NonNull
    public MaternityConfiguration getMaternityConfiguration() {
        return maternityConfiguration;
    }

    @NonNull
    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = Compressor.getDefault(context().applicationContext());
        }

        return compressor;
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }


    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    private void initializeYamlConfigs() {
        Constructor constructor = new Constructor(YamlConfig.class);
        TypeDescription customTypeDescription = new TypeDescription(YamlConfig.class);
        customTypeDescription.addPropertyParameters(YamlConfigItem.GENERIC_YAML_ITEMS, YamlConfigItem.class);
        constructor.addTypeDescription(customTypeDescription);
        yaml = new Yaml(constructor);
    }

    @NonNull
    public Iterable<Object> readYaml(@NonNull String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                context.applicationContext().getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    @NonNull
    public MaternityRulesEngineHelper getMaternityRulesEngineHelper() {
        if (maternityRulesEngineHelper == null) {
            maternityRulesEngineHelper = new MaternityRulesEngineHelper();
        }

        return maternityRulesEngineHelper;
    }

    @NonNull
    public Event processOpdCheckInForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONObject stepOne = jsonFormObject.getJSONObject(MaternityJsonFormUtils.STEP1);
        JSONArray fieldsArray = stepOne.getJSONArray(MaternityJsonFormUtils.FIELDS);

        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        String baseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.ENTITY_TABLE);
        Event opdCheckinEvent = MaternityJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable);

        AllSharedPreferences allSharedPreferences = MaternityUtils.getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        opdCheckinEvent.setProviderId(providerId);
        opdCheckinEvent.setLocationId(MaternityJsonFormUtils.locationId(allSharedPreferences));
        opdCheckinEvent.setFormSubmissionId(opdCheckinEvent.getFormSubmissionId());

        opdCheckinEvent.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        opdCheckinEvent.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        opdCheckinEvent.setClientDatabaseVersion(MaternityLibrary.getInstance().getDatabaseVersion());
        opdCheckinEvent.setClientApplicationVersion(MaternityLibrary.getInstance().getApplicationVersion());

        // Create the visit Id
        opdCheckinEvent.addDetails(MaternityConstants.Event.CheckIn.Detail.VISIT_ID, JsonFormUtils.generateRandomUUIDString());
        opdCheckinEvent.addDetails(MaternityConstants.Event.CheckIn.Detail.VISIT_DATE, MaternityUtils.convertDate(new Date(), MaternityDbConstants.DATE_FORMAT));

        return opdCheckinEvent;
    }

    public List<Event> processOpdDiagnosisAndTreatmentForm(@NonNull String jsonString, @NonNull Intent data) throws JSONException {
        JSONObject jsonFormObject = new JSONObject(jsonString);
        JSONObject step1JsonObject = jsonFormObject.optJSONObject(MaternityConstants.JSON_FORM_EXTRA.STEP1);

        JSONObject step2JsonObject = jsonFormObject.optJSONObject(MaternityConstants.JSON_FORM_EXTRA.STEP2);

        JSONObject step3JsonObject = jsonFormObject.optJSONObject(MaternityConstants.JSON_FORM_EXTRA.STEP3);

        JSONObject step4JsonObject = jsonFormObject.optJSONObject(MaternityConstants.JSON_FORM_EXTRA.STEP4);

        String entityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);

        OpdCheckIn opdCheckIn = MaternityLibrary.getInstance().getCheckInRepository().getLatestCheckIn(entityId);

        String visitId = opdCheckIn.getVisitId();

        List<JSONObject> steps = Arrays.asList(step1JsonObject, step2JsonObject, step3JsonObject, step4JsonObject);

        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        List<Event> eventList = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            JSONObject step = steps.get(i);
            JSONArray fields = step.getJSONArray(MaternityJsonFormUtils.FIELDS);
            String valueIds = null;
            JSONObject jsonObject;
            JSONArray valueJsonArray = null;
            if (i == 0 || i == 3) {
                valueIds = MaternityUtils.generateNIds(1);
            } else if (i == 1) {
                jsonObject = JsonFormUtils.getFieldJSONObject(fields, MaternityConstants.JSON_FORM_KEY.DISEASE_CODE);
                JSONObject jsonDiagnosisType = JsonFormUtils.getFieldJSONObject(fields, MaternityConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
                String diagnosisType = jsonDiagnosisType.optString(MaternityConstants.KEY.VALUE);
                String value = jsonObject.optString(MaternityConstants.KEY.VALUE);
                if (StringUtils.isBlank(value) || (new JSONArray(value).length() == 0)) {
                    valueIds = MaternityUtils.generateNIds(1);
                } else {
                    valueJsonArray = new JSONArray(value);
                    JSONArray jsonArrayWithOpenMrsIds = addOpenMrsEntityId(diagnosisType.toLowerCase(), valueJsonArray);
                    jsonObject.put(MaternityConstants.KEY.VALUE, jsonArrayWithOpenMrsIds);
                    valueIds = MaternityUtils.generateNIds(valueJsonArray.length());
                }
            } else if (i == 2) {
                jsonObject = JsonFormUtils.getFieldJSONObject(fields, MaternityConstants.JSON_FORM_KEY.MEDICINE);
                jsonObject.put(AllConstants.TYPE, AllConstants.MULTI_SELECT_LIST);
                String value = jsonObject.optString(MaternityConstants.KEY.VALUE);
                if (StringUtils.isBlank(value) || (new JSONArray(value).length() == 0)) {
                    valueIds = MaternityUtils.generateNIds(1);
                } else {
                    valueJsonArray = new JSONArray(value);
                    valueIds = MaternityUtils.generateNIds(valueJsonArray.length());
                }
            }
            Event baseEvent = JsonFormUtils.createEvent(fields, jsonFormObject.getJSONObject(METADATA),
                    formTag, entityId, getDiagnosisAndTreatmentEventArray()[i], getDiagnosisAndTreatmentTableArray()[i]);
            MaternityJsonFormUtils.tagSyncMetadata(baseEvent);
            baseEvent.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_ID, visitId);
            baseEvent.addDetails(MaternityConstants.JSON_FORM_KEY.ID, valueIds);
            if (valueJsonArray != null) {
                baseEvent.addDetails(MaternityConstants.KEY.VALUE, valueJsonArray.toString());
            }

            eventList.add(baseEvent);
        }

        //remove any saved sessions
        OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm = new OpdDiagnosisAndTreatmentForm(entityId);
        MaternityLibrary.getInstance().getOpdDiagnosisAndTreatmentFormRepository().delete(opdDiagnosisAndTreatmentForm);

        Event closeOpdVisit = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, entityId, MaternityConstants.EventType.CLOSE_OPD_VISIT, "");
        MaternityJsonFormUtils.tagSyncMetadata(closeOpdVisit);
        closeOpdVisit.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_ID, visitId);
        closeOpdVisit.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_END_DATE, MaternityUtils.convertDate(new Date(), MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeOpdVisit);

        return eventList;
    }

    private JSONArray addOpenMrsEntityId(String diagnosisType, JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, jsonObject.optJSONObject(JsonFormConstants.MultiSelectUtils.PROPERTY)
                        .optString(diagnosisType.concat("-id")));
            }

            return jsonArray;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return jsonArray;
    }

    protected String[] getDiagnosisAndTreatmentEventArray() {
        return new String[]{MaternityConstants.EventType.TEST_CONDUCTED, MaternityConstants.EventType.DIAGNOSIS,
                MaternityConstants.EventType.TREATMENT, MaternityConstants.EventType.SERVICE_DETAIL};
    }

    protected String[] getDiagnosisAndTreatmentTableArray() {
        return new String[]{MaternityDbConstants.Table.OPD_TEST_CONDUCTED, MaternityDbConstants.Table.OPD_DIAGNOSIS,
                MaternityDbConstants.Table.OPD_TREATMENT, MaternityDbConstants.Table.OPD_SERVICE_DETAIL};
    }

    public String opdLookUpQuery() {
        String lookUpQueryForChild = "select id as _id, %s, %s, %s, %s, %s, %s, zeir_id as %s, null as national_id from ec_child where [condition] ";
        lookUpQueryForChild = String.format(lookUpQueryForChild, MaternityConstants.KEY.RELATIONALID, MaternityConstants.KEY.FIRST_NAME,
                MaternityConstants.KEY.LAST_NAME, MaternityConstants.KEY.GENDER, MaternityConstants.KEY.DOB, MaternityConstants.KEY.BASE_ENTITY_ID, MaternityDbConstants.KEY.OPENSRP_ID);
        String lookUpQueryForMother = "select id as _id, %s, %s, %s, %s, %s, %s, register_id as %s, nrc_number as national_id from ec_mother where [condition] ";
        lookUpQueryForMother = String.format(lookUpQueryForMother, MaternityConstants.KEY.RELATIONALID, MaternityConstants.KEY.FIRST_NAME,
                MaternityConstants.KEY.LAST_NAME, MaternityConstants.KEY.GENDER, MaternityConstants.KEY.DOB, MaternityConstants.KEY.BASE_ENTITY_ID, MaternityDbConstants.KEY.OPENSRP_ID);
        String lookUpQueryForOpdClient = "select id as _id, %s, %s, %s, %s, %s, %s, %s, national_id from ec_client where [condition] ";
        lookUpQueryForOpdClient = String.format(lookUpQueryForOpdClient, MaternityConstants.KEY.RELATIONALID, MaternityConstants.KEY.FIRST_NAME,
                MaternityConstants.KEY.LAST_NAME, MaternityConstants.KEY.GENDER, MaternityConstants.KEY.DOB, MaternityConstants.KEY.BASE_ENTITY_ID, MaternityDbConstants.KEY.OPENSRP_ID);
        return lookUpQueryForChild + " union all " + lookUpQueryForMother + " union all " + lookUpQueryForOpdClient;
    }

    /**
     * This method enables us to configure how-long ago we should consider a valid check-in so that
     * we enable the next step which is DIAGNOSE & TREAT. This method returns the latest date that a check-in
     * should be so that it can be considered for moving to DIAGNOSE & TREAT
     *
     * @return Date
     */
    @NonNull
    public Date getLatestValidCheckInDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.getTime();
    }

    public boolean isPatientInTreatedState(@NonNull String strVisitEndDate) {
        Date visitEndDate = MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, strVisitEndDate);
        if (visitEndDate != null) {
            return isPatientInTreatedState(visitEndDate);
        }

        return false;
    }

    public boolean isPatientInTreatedState(@NonNull Date visitEndDate) {
        // Get the midnight of that day when the visit happened
        Calendar date = Calendar.getInstance();
        date.setTime(visitEndDate);
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);
        return getDateNow().before(date.getTime());
    }

    @VisibleForTesting
    @NonNull
    protected Date getDateNow() {
        return new Date();
    }

    /**
     * This checks if the patient can perform a Check-In evaluated based on their latest visit details & opd details. This however does not consider the TREATED status
     * which appears after a visit is completed within the same day. If you need to consider the TREATED status, you should first call {@link #isPatientInTreatedState(Date)}
     * and then call this method if the result is false.
     *
     * @param visit
     * @param maternityDetails
     * @return
     */
    public boolean canPatientCheckInInsteadOfDiagnoseAndTreat(@Nullable OpdVisit visit, @Nullable MaternityDetails maternityDetails) {
        Date latestValidCheckInDate = MaternityLibrary.getInstance().getLatestValidCheckInDate();

        // If we are past the 24 hours or so, then the status should be check-in
        // If your opd
        return visit == null || visit.getVisitDate().before(latestValidCheckInDate) || (maternityDetails != null && maternityDetails.getCurrentVisitEndDate() != null);
    }

    public boolean isClientCurrentlyCheckedIn(@Nullable OpdVisit opdVisit, @Nullable MaternityDetails maternityDetails) {
        return !canPatientCheckInInsteadOfDiagnoseAndTreat(opdVisit, maternityDetails);
    }
}

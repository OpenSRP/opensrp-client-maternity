package org.smartregister.maternity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.configuration.MaternityFormProcessingTask;
import org.smartregister.maternity.domain.YamlConfig;
import org.smartregister.maternity.domain.YamlConfigItem;
import org.smartregister.maternity.helper.MaternityRulesEngineHelper;
import org.smartregister.maternity.repository.MaternityChildRepository;
import org.smartregister.maternity.repository.MaternityOutcomeFormRepository;
import org.smartregister.maternity.repository.MaternityRegistrationDetailsRepository;
import org.smartregister.maternity.utils.AppExecutors;
import org.smartregister.maternity.utils.ConfigurationInstancesHelper;
import org.smartregister.maternity.utils.FilePath;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

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
    private MaternityRegistrationDetailsRepository maternityRegistrationDetailsRepository;
    private MaternityOutcomeFormRepository maternityOutcomeFormRepository;
    private MaternityChildRepository maternityChildRepository;
    private AppExecutors appExecutors;

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


    public static int getGestationAgeInWeeks(@NonNull String conceptionDateString) {
        DateTimeFormatter SQLITE_DATE_DF = DateTimeFormat.forPattern("dd-MM-yyyy");
        LocalDate conceptionDate = SQLITE_DATE_DF.withOffsetParsed().parseLocalDate(conceptionDateString);
        Weeks weeks = Weeks.weeksBetween(conceptionDate, LocalDate.now());
        return weeks.getWeeks();
    }

    @NonNull
    public Context context() {
        return context;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    @NonNull
    public MaternityRegistrationDetailsRepository getMaternityRegistrationDetailsRepository() {
        if (maternityRegistrationDetailsRepository == null) {
            maternityRegistrationDetailsRepository = new MaternityRegistrationDetailsRepository();
        }

        return maternityRegistrationDetailsRepository;
    }

    public MaternityChildRepository getMaternityChildRepository() {
        if (maternityChildRepository == null) {
            maternityChildRepository = new MaternityChildRepository();
        }
        return maternityChildRepository;
    }

    @NonNull
    public MaternityOutcomeFormRepository getMaternityOutcomeFormRepository() {
        if (maternityOutcomeFormRepository == null) {
            maternityOutcomeFormRepository = new MaternityOutcomeFormRepository();
        }
        return maternityOutcomeFormRepository;
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
            compressor = new Compressor(context().applicationContext());
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
    public List<Event> processMaternityOutcomeForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        HashMap<String, Class<? extends MaternityFormProcessingTask>> maternityFormProcessingTasks = getMaternityConfiguration().getMaternityFormProcessingTasks();
        List<Event> eventList = new ArrayList<>();
        if (maternityFormProcessingTasks.get(eventType) != null) {
            MaternityFormProcessingTask<List<Event>> maternityFormProcessingTask = ConfigurationInstancesHelper.newInstance(maternityFormProcessingTasks.get(eventType));
            eventList = maternityFormProcessingTask.processMaternityForm(jsonString, data);
        }
        return eventList;
    }

    @NonNull
    public List<Event> processMaternityCloseForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONArray fieldsArray = MaternityUtils.generateFieldsFromJsonForm(jsonFormObject);
        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        String baseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.ENTITY_TABLE);
        Event closeMaternityEvent = JsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable);
        MaternityJsonFormUtils.tagSyncMetadata(closeMaternityEvent);
        eventList.add(closeMaternityEvent);

        return eventList;
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

    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
    }
}

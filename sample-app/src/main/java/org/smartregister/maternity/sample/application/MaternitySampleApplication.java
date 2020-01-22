package org.smartregister.maternity.sample.application;

import android.support.annotation.NonNull;

import com.evernote.android.job.JobManager;

import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.maternity.processor.MaternityMiniClientProcessorForJava;
import org.smartregister.maternity.sample.BuildConfig;
import org.smartregister.maternity.sample.activity.MaternityFormActivity;
import org.smartregister.maternity.sample.configuration.MaternityRegisterQueryProvider;
import org.smartregister.maternity.sample.job.SampleMaternityJobCreator;
import org.smartregister.maternity.sample.configuration.SampleSyncConfiguration;
import org.smartregister.maternity.sample.processor.MaternitySampleClientProcessorForJava;
import org.smartregister.maternity.sample.repository.SampleRepository;
import org.smartregister.maternity.sample.utils.Constants;
import org.smartregister.maternity.sample.utils.Utils;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternitySampleApplication extends org.smartregister.view.activity.DrishtiApplication {

    private static CommonFtsObject commonFtsObject;

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{Constants.Table.CHILD, Constants.Table.MOTHER, MaternityDbConstants.KEY.TABLE};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(Constants.Table.CHILD)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.MIDDLE_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        } else if (tableName.equals(Constants.Table.MOTHER)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.MIDDLE_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        } else if (tableName.equals(MaternityDbConstants.KEY.TABLE)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        }

        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(Constants.Table.CHILD)) {
            List<String> names = new ArrayList<>();
            names.add(Constants.Columns.FIRST_NAME);
            names.add(Constants.Columns.MIDDLE_NAME);
            names.add(Constants.Columns.LAST_NAME);
            names.add(Constants.Columns.DOB);

            return names.toArray(new String[names.size()]);
        } else if (tableName.equals(MaternityDbConstants.KEY.TABLE)){

            return new String[]{MaternityDbConstants.KEY.BASE_ENTITY_ID, MaternityDbConstants.KEY.FIRST_NAME, MaternityDbConstants.KEY.LAST_NAME,
                    MaternityDbConstants.KEY.LAST_INTERACTED_WITH, MaternityDbConstants.KEY.DATE_REMOVED};
        }
        return null;
    }

    public static synchronized MaternitySampleApplication getInstance() {
        return (MaternitySampleApplication) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context, new SampleSyncConfiguration());

        //Opd Initialization
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.Form.MATERNITY_REGISTRATION
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , MaternityConstants.CONFIG
                , MaternityFormActivity.class
                , BaseMaternityProfileActivity.class
                ,true);
        MaternityConfiguration maternityConfiguration = new MaternityConfiguration
                .Builder(MaternityRegisterQueryProvider.class)
                .setMaternityMetadata(maternityMetadata)
                .build();
        MaternityLibrary.init(context, getRepository(), maternityConfiguration, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(SampleRepository.PASSWORD);
        context.session().setPassword(SampleRepository.PASSWORD);

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        //init Job Manager
        JobManager.create(this).addJobCreator(new SampleMaternityJobCreator());
        sampleUniqueIds();
        initializeTestLocationData();
    }

    @Override
    public void logoutCurrentUser() {
    }

    public void initializeTestLocationData(){
        // this function is for test purposes only
        String loc = "{\"locationsHierarchy\":{\"map\":{\"5b854508-42c5-4cc5-9bea-77335687a428\":{\"children\":{\"425b0ac3-05e7-4123-ad27-76f510d96a6a\":{\"children\":{\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\":{\"children\":{\"6ca7788c-d995-4431-a8a3-2f030db1aee0\":{\"id\":\"6ca7788c-d995-4431-a8a3-2f030db1aee0\",\"label\":\"The crypts\",\"node\":{\"locationId\":\"6ca7788c-d995-4431-a8a3-2f030db1aee0\",\"name\":\"The crypts\",\"parentLocation\":{\"locationId\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\",\"name\":\"Winterfell\",\"parentLocation\":{\"locationId\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"name\":\"The North\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Facility\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\"}},\"id\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\",\"label\":\"Winterfell\",\"node\":{\"locationId\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\",\"name\":\"Winterfell\",\"parentLocation\":{\"locationId\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"name\":\"The North\",\"parentLocation\":{\"locationId\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"name\":\"Westeros\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Department\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\"}},\"id\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"label\":\"The North\",\"node\":{\"locationId\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"name\":\"The North\",\"parentLocation\":{\"locationId\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"name\":\"Westeros\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"5b854508-42c5-4cc5-9bea-77335687a428\"}},\"id\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"label\":\"Westeros\",\"node\":{\"locationId\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"name\":\"Westeros\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"425b0ac3-05e7-4123-ad27-76f510d96a6a\":[\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\"],\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\":[\"6ca7788c-d995-4431-a8a3-2f030db1aee0\"],\"5b854508-42c5-4cc5-9bea-77335687a428\":[\"425b0ac3-05e7-4123-ad27-76f510d96a6a\"]}}}";
        context.allSettings().saveANMLocation(loc);
        context.allSettings().put("dfltLoc-", "6ca7788c-d995-4431-a8a3-2f030db1aee0");
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new SampleRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }

    private void sampleUniqueIds() {
        List<String> ids = generateIds(250);
        MaternityLibrary.getInstance().getUniqueIdRepository().bulkInsertOpenmrsIds(ids);
    }

    private List<String> generateIds(int size) {
        List<String> ids = new ArrayList<>();
        Random r = new Random();

        for (int i = 10; i < size; i++) {
            Integer randomInt = r.nextInt(10000) + 1;
            ids.add(formatSampleId(randomInt.toString()));
        }

        return ids;
    }

    private String formatSampleId(String openmrsId) {
        int lastIndex = openmrsId.length() - 1;
        String tail = openmrsId.substring(lastIndex);
        return openmrsId.substring(0, lastIndex) + "-" + tail;
    }

    @NonNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return MaternitySampleClientProcessorForJava.getInstance(this);
    }

    public Context context() {
        return context;
    }

    @Override
    public String getPassword() {
        return SampleRepository.PASSWORD;
    }
}

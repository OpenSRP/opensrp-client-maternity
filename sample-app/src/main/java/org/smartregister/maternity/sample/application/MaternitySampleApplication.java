package org.smartregister.maternity.sample.application;

import com.evernote.android.job.JobManager;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.maternity.sample.BuildConfig;
import org.smartregister.maternity.sample.activity.MaternityFormActivity;
import org.smartregister.maternity.sample.configuration.MaternityRegisterQueryProvider;
import org.smartregister.maternity.sample.job.SampleMaternityJobCreator;
import org.smartregister.maternity.sample.configuration.SampleSyncConfiguration;
import org.smartregister.maternity.sample.repository.SampleRepository;
import org.smartregister.maternity.sample.utils.Constants;
import org.smartregister.maternity.sample.utils.Utils;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternitySampleApplication extends org.smartregister.view.activity.DrishtiApplication {

    private static final String TAG = MaternitySampleApplication.class.getCanonicalName();
    private static CommonFtsObject commonFtsObject;
    private boolean lastModified;

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
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.JSON_FORM_KEY.NAME
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.OPD_REGISTRATION
                , MaternityConstants.EventType.UPDATE_OPD_REGISTRATION
                , MaternityConstants.CONFIG
                , MaternityFormActivity.class
                ,null
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
    }

    @Override
    public void logoutCurrentUser() {
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
        List<String> ids = generateIds(20);
        MaternityLibrary.getInstance().getUniqueIdRepository().bulkInserOpenmrsIds(ids);
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

    public Context context() {
        return context;
    }
}

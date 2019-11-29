package org.smartregister.opd.sample.application;

import com.evernote.android.job.JobManager;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.opd.sample.BuildConfig;
import org.smartregister.opd.sample.activity.OpdFormActivity;
import org.smartregister.opd.sample.configuration.OpdRegisterQueryProvider;
import org.smartregister.opd.sample.job.SampleOpdJobCreator;
import org.smartregister.opd.sample.configuration.SampleSyncConfiguration;
import org.smartregister.opd.sample.repository.SampleRepository;
import org.smartregister.opd.sample.utils.Constants;
import org.smartregister.opd.sample.utils.Utils;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-18
 */

public class OpdSampleApplication extends org.smartregister.view.activity.DrishtiApplication {

    private static final String TAG = OpdSampleApplication.class.getCanonicalName();
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
        return new String[]{Constants.Table.CHILD, Constants.Table.MOTHER, OpdDbConstants.KEY.TABLE};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(Constants.Table.CHILD)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.MIDDLE_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        } else if (tableName.equals(Constants.Table.MOTHER)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.MIDDLE_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        } else if (tableName.equals(OpdDbConstants.KEY.TABLE)) {
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
        } else if (tableName.equals(OpdDbConstants.KEY.TABLE)){

            return new String[]{OpdDbConstants.KEY.BASE_ENTITY_ID, OpdDbConstants.KEY.FIRST_NAME, OpdDbConstants.KEY.LAST_NAME,
                    OpdDbConstants.KEY.LAST_INTERACTED_WITH, OpdDbConstants.KEY.DATE_REMOVED};
        }
        return null;
    }

    public static synchronized OpdSampleApplication getInstance() {
        return (OpdSampleApplication) mInstance;
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
        OpdMetadata opdMetadata = new OpdMetadata(OpdConstants.JSON_FORM_KEY.NAME
                , OpdDbConstants.KEY.TABLE
                , OpdConstants.EventType.OPD_REGISTRATION
                , OpdConstants.EventType.UPDATE_OPD_REGISTRATION
                , OpdConstants.CONFIG
                , OpdFormActivity.class
                ,null
                ,true);
        OpdConfiguration opdConfiguration = new OpdConfiguration
                .Builder(OpdRegisterQueryProvider.class)
                .setOpdMetadata(opdMetadata)
                .build();
        OpdLibrary.init(context, getRepository(), opdConfiguration, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(SampleRepository.PASSWORD);
        context.session().setPassword(SampleRepository.PASSWORD);

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        //init Job Manager
        JobManager.create(this).addJobCreator(new SampleOpdJobCreator());
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
        OpdLibrary.getInstance().getUniqueIdRepository().bulkInserOpenmrsIds(ids);
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

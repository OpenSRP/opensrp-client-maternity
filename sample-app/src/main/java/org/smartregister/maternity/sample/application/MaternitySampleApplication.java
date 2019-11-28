package org.smartregister.maternity.sample.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.JobManager;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.configuration.OpdConfiguration;
import org.smartregister.maternity.pojos.OpdMetadata;
import org.smartregister.maternity.sample.BuildConfig;
import org.smartregister.maternity.sample.configuration.OpdRegisterQueryProvider;
import org.smartregister.maternity.sample.configuration.SampleSyncConfiguration;
import org.smartregister.maternity.sample.job.SampleOpdJobCreator;
import org.smartregister.maternity.sample.repository.SampleRepository;
import org.smartregister.maternity.sample.utils.Constants;
import org.smartregister.maternity.utils.DefaultMaternityLocationHierarchyProvider;
import org.smartregister.maternity.utils.OpdConstants;
import org.smartregister.maternity.utils.OpdDbConstants;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-18
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

    @NonNull
    private static String[] getFtsTables() {
        return new String[]{Constants.Table.MATERNITY};
    }

    @Nullable
    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(Constants.Table.MATERNITY)) {
            return new String[]{Constants.Columns.Maternity.FIRST_NAME
                    , Constants.Columns.Maternity.MIDDLE_NAME
                    , Constants.Columns.Maternity.LAST_NAME
                    , Constants.Columns.Maternity.DOB
                    , Constants.Columns.Maternity.LAST_INTERACTED_WITH
            };
        }

        return null;
    }

    @Nullable
    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(Constants.Table.MATERNITY)) {
            List<String> names = new ArrayList<>();
            names.add(Constants.Columns.Maternity.FIRST_NAME);
            names.add(Constants.Columns.Maternity.MIDDLE_NAME);
            names.add(Constants.Columns.Maternity.LAST_NAME);
            names.add(Constants.Columns.Maternity.DOB);

            return names.toArray(new String[names.size()]);
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

        DefaultMaternityLocationHierarchyProvider defaultMaternityLocationHierarchyProvider = new DefaultMaternityLocationHierarchyProvider();

        //Maternity Initialization
        OpdMetadata opdMetadata = new OpdMetadata(OpdConstants.JSON_FORM_KEY.NAME
                , OpdDbConstants.KEY.TABLE
                , OpdConstants.EventType.OPD_REGISTRATION
                , OpdConstants.EventType.UPDATE_OPD_REGISTRATION
                , OpdConstants.CONFIG
                , null
                ,null
                , defaultMaternityLocationHierarchyProvider
                ,true);

        OpdConfiguration opdConfiguration = new OpdConfiguration
                .Builder(OpdRegisterQueryProvider.class)
                .setOpdMetadata(opdMetadata)
                .build();

        MaternityLibrary.init(context, getRepository(), opdConfiguration, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(SampleRepository.PASSWORD);
        context.session().setPassword(SampleRepository.PASSWORD);

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(defaultMaternityLocationHierarchyProvider.getAllowedLevels(), defaultMaternityLocationHierarchyProvider.getDefaultLevel());

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

package org.smartregister.opd.sample.configuration;

import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.opd.sample.BuildConfig;
import org.smartregister.opd.sample.application.OpdSampleApplication;
import org.smartregister.repository.AllSharedPreferences;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-18
 */

public class SampleSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences = OpdSampleApplication.getInstance().context().userService()
                .getAllSharedPreferences();
        return sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM());
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public boolean isSyncSettings() {
        return BuildConfig.IS_SYNC_SETTINGS;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return true;
    }

}
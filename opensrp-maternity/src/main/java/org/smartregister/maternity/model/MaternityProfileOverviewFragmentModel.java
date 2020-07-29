package org.smartregister.maternity.model;

import android.support.annotation.NonNull;

import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityProfileOverviewFragmentContract;
import org.smartregister.maternity.utils.AppExecutors;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityProfileOverviewFragmentModel implements MaternityProfileOverviewFragmentContract.Model {

    private AppExecutors appExecutors;
    private HashMap<String, String> maternityDetails = null;

    public MaternityProfileOverviewFragmentModel() {
        this.appExecutors = MaternityLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void fetchMaternityOverviewDetails(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(new Runnable() {

            @Override
            public void run() {
                maternityDetails = new HashMap<>();
                maternityDetails = MaternityLibrary.getInstance().getMaternityRegistrationDetailsRepository().findByBaseEntityId(baseEntityId);

                HashMap<String, String> maternityMedicInfo = MaternityLibrary.getInstance().getMaternityDetailsRepository().findMedicInfoByBaseEntityId(baseEntityId);
                if (maternityMedicInfo != null) {
                    maternityDetails.putAll(maternityMedicInfo);
                }

                appExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        onFetchedCallback.onFetched(maternityDetails);
                    }
                });
            }
        });
    }
}

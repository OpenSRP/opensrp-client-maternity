package org.smartregister.maternity.model;

import android.support.annotation.NonNull;

import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityProfileOverviewFragmentContract;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.pojos.OpdVisit;
import org.smartregister.maternity.utils.AppExecutors;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityProfileOverviewFragmentModel implements MaternityProfileOverviewFragmentContract.Model {

    private AppExecutors appExecutors;
    private MaternityDetails maternityDetails = null;

    public MaternityProfileOverviewFragmentModel() {
        this.appExecutors = new AppExecutors();
    }

    @Override
    public void fetchLastCheckAndVisit(final @NonNull String baseEntityId, @NonNull final OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(new Runnable() {

            @Override
            public void run() {
                /*final OpdVisit visit = MaternityLibrary.getInstance().getVisitRepository().getLatestVisit(baseEntityId);
                final OpdCheckIn checkIn = visit != null ? MaternityLibrary.getInstance().getCheckInRepository().getCheckInByVisit(visit.getId()) : null;

                maternityDetails = null;

                if (visit != null) {
                    maternityDetails = new MaternityDetails(baseEntityId, visit.getId());
                    maternityDetails = MaternityLibrary.getInstance().getMaternityDetailsRepository().findOne(maternityDetails);
                }

                appExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        onFetchedCallback.onFetched(checkIn, visit, maternityDetails);
                    }
                });*/
            }
        });
    }
}

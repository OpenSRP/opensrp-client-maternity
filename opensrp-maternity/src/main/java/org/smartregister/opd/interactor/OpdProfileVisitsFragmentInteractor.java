package org.smartregister.opd.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.utils.AppExecutors;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileVisitsFragmentInteractor implements OpdProfileVisitsFragmentContract.Interactor {

    private OpdProfileVisitsFragmentContract.Presenter mProfileFrgamentPresenter;
    private AppExecutors appExecutors;

    public OpdProfileVisitsFragmentInteractor(@NonNull OpdProfileVisitsFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
        appExecutors = new AppExecutors();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit) {
        // Todo: We will have an implementation for refresh view
    }

    @Override
    public void fetchVisits(@NonNull final String baseEntityId, final int pageNo, @NonNull final OpdProfileVisitsFragmentContract.Presenter.OnVisitsLoadedCallback onVisitsLoadedCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<OpdVisitSummary> summaries = OpdLibrary.getInstance().getOpdVisitSummaryRepository().getOpdVisitSummaries(baseEntityId, pageNo);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onVisitsLoadedCallback.onVisitsLoaded(summaries);
                    }
                });
            }
        });
    }

    @Override
    public void fetchVisitsPageCount(@NonNull final String baseEntityId, @NonNull final OnFetchVisitsPageCountCallback onFetchVisitsPageCountCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final int visitsPageCount = OpdLibrary.getInstance().getOpdVisitSummaryRepository().getVisitPageCount(baseEntityId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onFetchVisitsPageCountCallback.onFetchVisitsPageCount(visitsPageCount);
                    }
                });
            }
        });
    }

    @Nullable
    public OpdProfileVisitsFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}
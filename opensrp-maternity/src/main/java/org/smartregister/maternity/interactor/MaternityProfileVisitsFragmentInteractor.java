package org.smartregister.maternity.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityProfileVisitsFragmentContract;
import org.smartregister.maternity.pojos.OpdVisitSummary;
import org.smartregister.maternity.utils.AppExecutors;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class MaternityProfileVisitsFragmentInteractor implements MaternityProfileVisitsFragmentContract.Interactor {

    private MaternityProfileVisitsFragmentContract.Presenter mProfileFrgamentPresenter;
    private AppExecutors appExecutors;

    public MaternityProfileVisitsFragmentInteractor(@NonNull MaternityProfileVisitsFragmentContract.Presenter presenter) {
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
    public void fetchVisits(@NonNull final String baseEntityId, final int pageNo, @NonNull final MaternityProfileVisitsFragmentContract.Presenter.OnVisitsLoadedCallback onVisitsLoadedCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<OpdVisitSummary> summaries = MaternityLibrary.getInstance().getOpdVisitSummaryRepository().getOpdVisitSummaries(baseEntityId, pageNo);

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
                final int visitsPageCount = MaternityLibrary.getInstance().getOpdVisitSummaryRepository().getVisitPageCount(baseEntityId);

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
    public MaternityProfileVisitsFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}
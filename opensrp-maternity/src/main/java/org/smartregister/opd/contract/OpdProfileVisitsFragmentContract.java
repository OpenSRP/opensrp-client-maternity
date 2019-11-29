package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.pojos.OpdVisitSummaryResultModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 *
 */
public interface OpdProfileVisitsFragmentContract {

    interface Presenter {

        @Nullable
        OpdProfileVisitsFragmentContract.View getProfileView();

        void onDestroy(boolean isChangingConfiguration);

        void loadVisits(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void loadPageCounter(@NonNull String baseEntityId);

        void populateWrapperDataAndFacts(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items);

        void onNextPageClicked();

        void onPreviousPageClicked();

        @NonNull
        String generateMedicationText(@NonNull HashMap<String, OpdVisitSummaryResultModel.Treatment> treatments);

        @NonNull
        String generateDiseasesText(@NonNull OpdVisitSummary opdVisitSummary);

        interface OnFinishedCallback {

            void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items);
        }

        interface OnVisitsLoadedCallback {

            void onVisitsLoaded(@NonNull List<OpdVisitSummary> opdVisitSummaries);
        }
    }

    interface View {

        String getString(@StringRes int resId);

        void showPageCountText(@NonNull String pageCounter);

        void showNextPageBtn(boolean show);

        void showPreviousPageBtn(boolean show);

        void displayVisits(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items);

        @Nullable
        String getClientBaseEntityId();

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);

        void fetchVisits(@NonNull String baseEntityId, int pageNo, @NonNull Presenter.OnVisitsLoadedCallback onVisitsLoadedCallback);

        void fetchVisitsPageCount(@NonNull String baseEntityId, @NonNull OnFetchVisitsPageCountCallback onTotalVisitCountCallback);

        interface OnFetchVisitsPageCountCallback {

            void onFetchVisitsPageCount(int visitsPageCount);
        }
    }
}
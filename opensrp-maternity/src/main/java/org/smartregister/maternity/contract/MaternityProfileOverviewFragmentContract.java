package org.smartregister.maternity.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.pojos.OpdVisit;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface MaternityProfileOverviewFragmentContract {

    interface View {

        @Nullable
        String getString(@StringRes int stringId);

    }

    interface Presenter {

        void loadOverviewFacts(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void loadOverviewDataAndDisplay(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable MaternityDetails maternityDetails, @NonNull final OnFinishedCallback onFinishedCallback);

        void setDataFromCheckIn(@Nullable OpdCheckIn checkIn, @Nullable OpdVisit visit, @Nullable MaternityDetails maternityDetails, @NonNull Facts facts);

        void setClient(@NonNull CommonPersonObjectClient client);

        @Nullable
        View getProfileView();

        @Nullable
        String getString(@StringRes int stringId);

        interface OnFinishedCallback {

            void onFinished(@Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal);
        }
    }

    interface Model {

        void fetchLastCheckAndVisit(@NonNull String baseEntityId, @NonNull OnFetchedCallback onFetchedCallback);

        interface OnFetchedCallback {

            void onFetched(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable MaternityDetails maternityDetails);
        }
    }
}

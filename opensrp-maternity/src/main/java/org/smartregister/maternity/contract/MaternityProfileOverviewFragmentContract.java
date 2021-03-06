package org.smartregister.maternity.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.domain.YamlConfigWrapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface MaternityProfileOverviewFragmentContract {

    interface View {

        @Nullable
        String getString(@StringRes int stringId);

        @Nullable
        CommonPersonObjectClient getActivityClientMap();

    }

    interface Presenter {

        void loadOverviewFacts(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void loadOverviewDataAndDisplay(@NonNull HashMap<String, String> maternityDetails, @NonNull final OnFinishedCallback onFinishedCallback);

        void setDataFromRegistration(@NonNull HashMap<String, String> maternityDetails, @NonNull Facts facts);

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

        void fetchMaternityOverviewDetails(@NonNull String baseEntityId, @NonNull OnFetchedCallback onFetchedCallback);

        interface OnFetchedCallback {

            void onFetched(@NonNull HashMap<String, String> maternityDetails);
        }
    }
}

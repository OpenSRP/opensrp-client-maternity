package org.smartregister.maternity.contract;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.listener.OnSendActionToFragment;
import org.smartregister.maternity.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.maternity.pojos.MaternityEventClient;
import org.smartregister.maternity.pojos.RegisterParams;
import org.smartregister.view.contract.BaseProfileContract;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface MaternityProfileActivityContract {

    interface Presenter extends BaseProfileContract.Presenter {

        @Nullable
        MaternityProfileActivityContract.View getProfileView();

        void refreshProfileTopSection(@NonNull Map<String, String> client);

        void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient);

        void startFormActivity(@Nullable JSONObject form, @NonNull String caseId, @NonNull String entityTable);

        void saveVisitOrDiagnosisForm(@NonNull String eventType, @Nullable Intent data);

        void saveUpdateRegistrationForm(@NonNull String jsonString, @NonNull RegisterParams registerParams);

        @Nullable
        MaternityEventClient processRegistration(@NonNull String jsonString, @NonNull FormTag formTag);

        void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId);
    }

    interface View extends BaseProfileContract.View {

        void setProfileName(@NonNull String fullName);

        void setProfileID(@NonNull String registerId);

        void setProfileAge(@NonNull String age);

        void setProfileGender(@NonNull String gender);

        void setProfileImage(@NonNull String baseEntityId);

        void openDiagnoseAndTreatForm();

        void openCheckInForm();

        void startFormActivity(@NonNull JSONObject form, @NonNull HashMap<String, String> intentKeys);

        OnSendActionToFragment getActionListenerForVisitFragment();

        OnSendActionToFragment getActionListenerForProfileOverview();

        @Nullable
        String getString(@StringRes int resId);


        @NonNull
        Context getContext();

        void startActivityForResult(@NonNull Intent intent, int requestCode);

        @Nullable
        CommonPersonObjectClient getClient();

        void setClient(@NonNull CommonPersonObjectClient client);

    }

    interface Interactor {

        void fetchSavedDiagnosisAndTreatmentForm(@NonNull String baseEntityId, @NonNull String entityTable);

        void saveRegistration(@NonNull MaternityEventClient maternityEventClient, @NonNull String jsonString, RegisterParams registerParams, @NonNull MaternityProfileActivityContract.InteractorCallBack callBack);

        @Nullable
        CommonPersonObjectClient retrieveUpdatedClient(@NonNull String baseEntityId);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void onRegistrationSaved(@Nullable CommonPersonObjectClient client, boolean isEdit);

        void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable);

    }
}
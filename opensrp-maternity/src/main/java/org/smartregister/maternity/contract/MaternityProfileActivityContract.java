package org.smartregister.maternity.contract;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.listener.OnSendActionToFragment;
import org.smartregister.maternity.listener.OngoingTaskCompleteListener;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.MaternityPartialForm;
import org.smartregister.maternity.pojo.OngoingTask;
import org.smartregister.maternity.pojo.RegisterParams;
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

        void saveOutcomeForm(@NonNull String eventType, @Nullable Intent data);

        void saveMedicInfoForm(@NonNull String eventType, @Nullable Intent data);

        void saveMaternityCloseForm(@NonNull String eventType, @Nullable Intent data);

        void saveUpdateRegistrationForm(@NonNull String jsonString, @NonNull RegisterParams registerParams);

        @Nullable
        MaternityEventClient processRegistration(@NonNull String jsonString, @NonNull FormTag formTag);

        void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId);

        boolean hasOngoingTask();

        OngoingTask getOngoingTask();

        boolean setOngoingTask(@NonNull OngoingTask ongoingTask);

        boolean removeOngoingTask(@NonNull OngoingTask ongoingTask);

        boolean addOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener);

        boolean removeOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener);

        boolean hasAncProfile();
    }

    interface View extends BaseProfileContract.View {

        void setProfileName(@NonNull String fullName);

        void setProfileID(@NonNull String registerId);

        void setProfileAge(@NonNull String age);

        void setProfileGender(@NonNull String gender);

        void setProfileImage(@NonNull String baseEntityId);

        void openMaternityOutcomeForm();

        void openMaternityMedicInfoForm();

        void openMaternityCloseForm();

        void startFormActivity(@NonNull JSONObject form, @NonNull HashMap<String, String> intentData);

        OnSendActionToFragment getActionListenerForVisitFragment();

        OnSendActionToFragment getActionListenerForProfileOverview();

        @Nullable
        String getString(@StringRes int resId);

        @NonNull
        Context getContext();

        void startActivityForResult(Intent intent, int requestCode);

        @Nullable
        CommonPersonObjectClient getClient();

        void setClient(@NonNull CommonPersonObjectClient client);

        void showMessage(@Nullable String text);

        void closeView();

    }

    interface Interactor {

        void fetchSavedPartialForm(@Nullable String formType, @NonNull String baseEntityId, @NonNull String entityTable);

        void saveRegistration(@NonNull MaternityEventClient maternityEventClient, @NonNull String jsonString, RegisterParams registerParams, @NonNull MaternityProfileActivityContract.InteractorCallBack callBack);

        @Nullable
        CommonPersonObjectClient retrieveUpdatedClient(@NonNull String baseEntityId);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void onRegistrationSaved(@Nullable CommonPersonObjectClient client, boolean isEdit);

        void onFetchedSavedPartialForm(@Nullable MaternityPartialForm savedPartialForm, @NonNull String caseId, @NonNull String entityTable);

    }
}
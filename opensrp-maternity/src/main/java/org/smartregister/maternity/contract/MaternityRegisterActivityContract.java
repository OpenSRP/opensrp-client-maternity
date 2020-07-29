package org.smartregister.maternity.contract;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.MaternityPartialForm;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface MaternityRegisterActivityContract {

    interface View extends BaseRegisterContract.View {

        MaternityRegisterActivityContract.Presenter presenter();

        void startFormActivityFromFormName(String formName, String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String clientTable);

        void startFormActivityFromFormJson(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData);
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void saveForm(String jsonString, @NonNull RegisterParams registerParams);

        void saveOutcomeForm(@NonNull String eventType, @Nullable Intent data);

        void saveMedicInfoForm(@NonNull String eventType, @Nullable Intent data);

        void startForm(@NonNull String formName, @Nullable String entityId, String metaData, @NonNull String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable);

        @NonNull
        MaternityRegisterActivityContract.Interactor createInteractor();
    }

    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        List<MaternityEventClient> processRegistration(String jsonString, FormTag formTag);

        @Nullable
        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws JSONException;

        @Nullable
        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException;

        String getInitials();

    }

    interface Interactor {

        void fetchSavedMaternityOutcomeForm(@NonNull String baseEntityId, @Nullable String entityTable, @NonNull InteractorCallBack interactorCallBack);

        void getNextUniqueId(Triple<String, String, String> triple, MaternityRegisterActivityContract.InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

        void saveRegistration(List<MaternityEventClient> maternityEventClientList, String jsonString, RegisterParams registerParams, MaternityRegisterActivityContract.InteractorCallBack callBack);

        void saveEvents(@NonNull List<Event> events, @NonNull InteractorCallBack callBack);
    }

    interface InteractorCallBack {

        void onNoUniqueId();

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onRegistrationSaved(boolean isEdit);

        void onEventSaved(List<Event> events);

        void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable MaternityPartialForm diagnosisAndTreatmentForm, @NonNull String caseId, @Nullable String entityTable);

    }
}
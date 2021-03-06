package org.smartregister.maternity.interactor;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.MaternityPartialForm;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.maternity.utils.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BaseMaternityRegisterActivityInteractor implements MaternityRegisterActivityContract.Interactor {

    protected AppExecutors appExecutors;

    public BaseMaternityRegisterActivityInteractor() {
        this(MaternityLibrary.getInstance().getAppExecutors());
    }

    @VisibleForTesting
    BaseMaternityRegisterActivityInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void fetchSavedPartialForm(final @Nullable String formType, final @NonNull String baseEntityId, final @Nullable String entityTable, @NonNull final MaternityRegisterActivityContract.InteractorCallBack interactorCallBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final MaternityPartialForm diagnosisAndTreatmentForm = MaternityLibrary
                        .getInstance()
                        .getMaternityPartialFormRepository()
                        .findOne(new MaternityPartialForm(baseEntityId, formType));

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        interactorCallBack.onFetchSavedPartialForm(diagnosisAndTreatmentForm, baseEntityId, entityTable);
                    }
                });
            }
        });
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final MaternityRegisterActivityContract.InteractorCallBack callBack) {
        // Do nothing for now, this will be handled by the class that extends this
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        // Do nothing for now, this will be handled by the class that extends this to nullify the presenter
    }

    @Override
    public void saveRegistration(List<MaternityEventClient> maternityEventClientList, String jsonString, RegisterParams registerParams, MaternityRegisterActivityContract.InteractorCallBack callBack) {
        // Do nothing for now, this will be handled by the class that extends this
    }

    @Override
    public void saveEvents(@NonNull final List<Event> events, @NonNull final MaternityRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<String> formSubmissionIds = new ArrayList<>();

                for (Event event : events) {
                    formSubmissionIds.add(event.getFormSubmissionId());
                    saveEventInDb(event);
                }

                processLatestUnprocessedEvents(formSubmissionIds);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onEventSaved(events);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }


    private void saveEventInDb(@NonNull Event event) {
        try {
            CoreLibrary.getInstance()
                    .context()
                    .getEventClientRepository()
                    .addEvent(event.getBaseEntityId()
                            , new JSONObject(JsonFormUtils.gson.toJson(event)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void processLatestUnprocessedEvents(List<String> formSubmissionIds) {
        // Process this event
        try {
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(formSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @NonNull
    public ECSyncHelper getSyncHelper() {
        return MaternityLibrary.getInstance().getEcSyncHelper();
    }

    @NonNull
    public AllSharedPreferences getAllSharedPreferences() {
        return MaternityLibrary.getInstance().context().allSharedPreferences();
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        return MaternityLibrary.getInstance().getUniqueIdRepository();
    }

}
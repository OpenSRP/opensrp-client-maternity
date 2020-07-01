package org.smartregister.maternity.utils;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.listener.MaternityEventActionCallBack;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class MaternityEventUtils {

    private AppExecutors appExecutors;
    private MaternityLibrary maternityLibrary;

    public MaternityEventUtils() {
        this.appExecutors = MaternityLibrary.getInstance().getAppExecutors();
        this.maternityLibrary = MaternityLibrary.getInstance();
    }

    public void saveEvents(@NonNull final List<Event> events, @NonNull final MaternityEventActionCallBack callBack) {
        Runnable runnable = () -> {
            List<String> formSubmissionIds = new ArrayList<>();
            for (Event event : events) {
                formSubmissionIds.add(event.getFormSubmissionId());
                saveEventInDb(event);
            }
            processLatestUnprocessedEvents(formSubmissionIds);
            appExecutors.mainThread().execute(() -> callBack.onMaternityEventSaved());
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
            maternityLibrary.getClientProcessorForJava().processClient(maternityLibrary.getEcSyncHelper().getEvents(formSubmissionIds));
            MaternityUtils.getAllSharedPreferences().saveLastUpdatedAtDate(new Date().getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}

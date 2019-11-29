package org.smartregister.opd.utils;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.listener.OpdEventActionCallBack;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.JsonFormUtils;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class OpdEventUtils {

    private AppExecutors appExecutors;
    private OpdLibrary opdLibrary;

    public OpdEventUtils(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        this.opdLibrary = OpdLibrary.getInstance();
    }

    public void saveEvents(@NonNull final List<Event> events, @NonNull final OpdEventActionCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (Event event : events) {
                    saveEventInDb(event);
                }

                processLatestUnprocessedEvents();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onOpdEventSaved();
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

    private void processLatestUnprocessedEvents() {
        // Process this event
        long lastSyncTimeStamp = OpdUtils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        try {
            opdLibrary.getClientProcessorForJava().processClient(opdLibrary.getEcSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            OpdUtils.getAllSharedPreferences().saveLastUpdatedAtDate(new Date().getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}

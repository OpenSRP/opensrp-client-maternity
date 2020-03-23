package org.smartregister.maternity.processor;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.MaternityCloseEventProcessException;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private HashSet<String> eventTypes = null;

    public MaternityMiniClientProcessorForJava(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes == null) {
            eventTypes = new HashSet<>();
            eventTypes.add(MaternityConstants.EventType.MATERNITY_REGISTRATION);
            eventTypes.add(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_OUTCOME);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_CLOSE);
        }

        return eventTypes;
    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {
        Event event = eventClient.getEvent();

        String eventType = event.getEventType();

        if (eventType.equals(MaternityConstants.EventType.MATERNITY_REGISTRATION)
                || eventType.equals(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION)) {
            ArrayList<EventClient> eventClients = new ArrayList<>();
            eventClients.add(eventClient);
            processClient(eventClients);

            updateRegisterTypeColumn(event, "maternity");

            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues);


            String conceptionDate = keyValues.get(MaternityConstants.Event.MaternityRegistration.CONCEPTION_DATE);
            String gravida = keyValues.get(MaternityConstants.Event.MaternityRegistration.GRAVIDA);
            String para = keyValues.get(MaternityConstants.Event.MaternityRegistration.PARA);
            String currentHivStatus = keyValues.get(MaternityConstants.Event.MaternityRegistration.CURRENT_HIV_STATUS);
            String previousHivStatus = keyValues.get(MaternityConstants.Event.MaternityRegistration.PREVIOUS_HIV_STATUS);

            if (gravida != null && conceptionDate != null) {

                MaternityDetails maternityDetails = new MaternityDetails(eventClient.getClient().getBaseEntityId(), gravida, conceptionDate);
                maternityDetails.setPara(para);
                maternityDetails.setHivStatus(currentHivStatus != null ? currentHivStatus : previousHivStatus);
                maternityDetails.setCreatedAt(new Date());

                //TODO: Figure out how to reuse the already created repository
                MaternityLibrary.getInstance().getMaternityDetailsRepository().saveOrUpdate(maternityDetails);
            } else {
                Timber.e(new Exception("Maternity Registration Event skipped for missing gravida or conceptionDate"));
            }
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new MaternityCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), MaternityConstants.EventType.MATERNITY_CLOSE));
            }

            unsyncEvents.add(event);
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);

                if (!TextUtils.isEmpty(value)) {
                    keyValues.put(key, value);
                    continue;
                }
            }

            List<Object> values = observation.getValues();
            if (values.size() > 0) {
                String value = (String) values.get(0);

                if (!TextUtils.isEmpty(value)) {
                    keyValues.put(key, value);
                    continue;
                }
            }
        }
    }

    private void abortTransaction() {
        if (MaternityLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        if (events != null) {
            for (Event event : events) {
                if (MaternityConstants.EventType.MATERNITY_CLOSE.equals(event.getEventType())) {
                    // Delete the maternity details
                    MaternityLibrary.getInstance().getMaternityDetailsRepository().delete(event.getBaseEntityId());

                    // Delete the actual client in the maternity table OR REMOVE THE Maternity register type
                    updateRegisterTypeColumn(event, null);
                }
            }
        }
        return true;
    }

    private void updateRegisterTypeColumn(@NonNull Event event, @Nullable String registerType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.Client.REGISTER_TYPE, registerType);

        MaternityLibrary.getInstance().context().commonrepository(MaternityDbConstants.Table.EC_CLIENT)
                .updateColumn(MaternityDbConstants.Table.EC_CLIENT, contentValues, event.getBaseEntityId());

        MaternityLibrary.getInstance().getRepository().getWritableDatabase()
                .update(CommonFtsObject.searchTableName(MaternityDbConstants.Table.EC_CLIENT)
                        , contentValues, MaternityDbConstants.Column.Client.BASE_ENTITY_ID + " = ?", new String[]{event.getBaseEntityId()});
    }
}
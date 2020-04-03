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
import org.smartregister.maternity.pojos.MaternityRegistrationDetails;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

            //updateRegisterTypeColumn(event, "maternity");

            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues, true);

            MaternityRegistrationDetails maternityDetails = new MaternityRegistrationDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
            maternityDetails.setCreatedAt(new Date());

            MaternityLibrary.getInstance().getMaternityRegistrationDetailsRepository().saveOrUpdate(maternityDetails);
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new MaternityCloseEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), MaternityConstants.EventType.MATERNITY_CLOSE));
            }

            unsyncEvents.add(event);
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_OUTCOME)) {
            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues);

            MaternityDetails maternityDetails = new MaternityDetails(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate(), keyValues);
            maternityDetails.setCreatedAt(new Date());
            MaternityLibrary.getInstance().getMaternityOutcomeDetailsRepository().saveOrUpdate(maternityDetails);
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues, boolean appendOnNewline) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);
                value = value != null ? value.trim() : value;

                if (!TextUtils.isEmpty(value)) {
                    if (appendOnNewline && keyValues.containsKey(key)) {
                        String currentValue = keyValues.get(key);
                        keyValues.put(key, value + "\n" + currentValue);
                    } else {
                        keyValues.put(key, value);
                    }
                    continue;
                }
            }

            List<Object> values = observation.getValues();
            if (values.size() > 0) {
                String value = (String) values.get(0);
                value = value != null ? value.trim() : value;

                if (!TextUtils.isEmpty(value)) {
                    if (appendOnNewline && keyValues.containsKey(key)) {
                        String currentValue = keyValues.get(key);
                        keyValues.put(key, value + "\n" + currentValue);
                    } else {
                        keyValues.put(key, value);
                    }
                }
            }
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues) {
        generateKeyValuesFromEvent(event, keyValues, false);
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        if (events != null) {
            for (Event event : events) {
                if (MaternityConstants.EventType.MATERNITY_CLOSE.equals(event.getEventType())) {
                    // Delete the maternity details
                    // MaternityLibrary.getInstance().getMaternityOutcomeDetailsRepository().delete(event.getBaseEntityId());

                    // Delete the actual client in the maternity table OR REMOVE THE Maternity register type
                    //updateRegisterTypeColumn(event, null);
                }
            }
        }
        return true;
    }
}
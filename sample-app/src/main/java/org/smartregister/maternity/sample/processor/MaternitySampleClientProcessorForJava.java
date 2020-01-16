package org.smartregister.maternity.sample.processor;

import android.content.Context;
import android.support.annotation.NonNull;

import org.smartregister.anc.library.sync.MiniClientProcessorForJava;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.maternity.processor.MaternityMiniClientProcessorForJava;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-01-16
 */

public class MaternitySampleClientProcessorForJava extends ClientProcessorForJava {

    private static MaternitySampleClientProcessorForJava instance;

    private HashMap<String, MiniClientProcessorForJava> processorMap = new HashMap<>();
    private HashMap<MiniClientProcessorForJava, List<Event>> unsyncEventsPerProcessor = new HashMap<>();

    private MaternitySampleClientProcessorForJava(Context context) {
        super(context);
        MaternityMiniClientProcessorForJava opdMiniClientProcessorForJava = new MaternityMiniClientProcessorForJava(context);
        addMiniProcessors(opdMiniClientProcessorForJava);
    }

    private void addMiniProcessors(MiniClientProcessorForJava... miniClientProcessorsForJava) {
        for (MiniClientProcessorForJava miniClientProcessorForJava : miniClientProcessorsForJava) {
            unsyncEventsPerProcessor.put(miniClientProcessorForJava, new ArrayList<Event>());

            HashSet<String> eventTypes = miniClientProcessorForJava.getEventTypes();

            for (String eventType : eventTypes) {
                processorMap.put(eventType, miniClientProcessorForJava);
            }
        }
    }


    public static MaternitySampleClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new MaternitySampleClientProcessorForJava(context);
        }
        return instance;
    }

    @Override
    public synchronized void processClient(List<EventClient> eventClients) throws Exception {

        ClientClassification clientClassification = assetJsonToJava("ec_client_classification.json",
                ClientClassification.class);

        if (!eventClients.isEmpty()) {
            List<Event> unsyncEvents = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                } else if (processorMap.containsKey(eventType)) {
                    try {
                        processEventUsingMiniprocessor(clientClassification, eventClient, eventType);
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
            }

            // Unsync events that are should not be in this device
            processUnsyncEvents(unsyncEvents);
        }
    }

    private void processUnsyncEvents(@NonNull List<Event> unsyncEvents) {
        if (!unsyncEvents.isEmpty()) {
            unSync(unsyncEvents);
        }

        for (MiniClientProcessorForJava miniClientProcessorForJava : unsyncEventsPerProcessor.keySet()) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            miniClientProcessorForJava.unSync(processorUnsyncEvents);
        }
    }

    private void processBirthAndWomanRegistrationEvent(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull Event event) {
        Client client = eventClient.getClient();
        //iterate through the events
        if (client != null) {
            try {
                processEvent(event, client, clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processEventUsingMiniprocessor(ClientClassification clientClassification, EventClient eventClient, String eventType) throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = processorMap.get(eventType);
        if (miniClientProcessorForJava != null) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            if (processorUnsyncEvents == null) {
                processorUnsyncEvents = new ArrayList<Event>();
                unsyncEventsPerProcessor.put(miniClientProcessorForJava, processorUnsyncEvents);
            }

            miniClientProcessorForJava.processEventClient(eventClient, processorUnsyncEvents, clientClassification);
        }
    }

    private boolean unSync(List<Event> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            ClientField clientField = assetJsonToJava("ec_client_fields.json", ClientField.class);
            return clientField != null;

        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{"zeir_id"};
    }
}
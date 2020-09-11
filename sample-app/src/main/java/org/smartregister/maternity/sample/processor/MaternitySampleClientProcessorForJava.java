package org.smartregister.maternity.sample.processor;

import android.content.Context;
import androidx.annotation.NonNull;

import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.maternity.processor.MaternityMiniClientProcessorForJava;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-01-16
 */

public class MaternitySampleClientProcessorForJava extends ClientProcessorForJava {

    private MaternitySampleClientProcessorForJava(Context context) {
        super(context);
        MaternityMiniClientProcessorForJava maternityMiniClientProcessorForJava = new MaternityMiniClientProcessorForJava(context);
        addMiniProcessors(maternityMiniClientProcessorForJava);
    }


    public static MaternitySampleClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new MaternitySampleClientProcessorForJava(context);
        }
        return (MaternitySampleClientProcessorForJava) instance;
    }

    @Override
    public synchronized void processClient(List<EventClient> eventClients) throws Exception {
        super.processClient(eventClients);

        for (List<Event> unprocessedEvents: unsyncEventsPerProcessor.values()) {
            processUnsyncEvents(unprocessedEvents);
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
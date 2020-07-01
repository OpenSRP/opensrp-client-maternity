package org.smartregister.maternity.pojo;

import android.support.annotation.NonNull;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class MaternityEventClient {

    private Event event;
    private Client client;

    public MaternityEventClient(@NonNull Client client, @NonNull Event event) {
        this.client = client;
        this.event = event;
    }

    @NonNull
    public Client getClient() {
        return client;
    }

    @NonNull
    public Event getEvent() {
        return event;
    }
}

package org.smartregister.maternity.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.smartregister.maternity.utils.MaternityJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static org.smartregister.util.JsonFormUtils.getFieldValue;

public class MaternityCloseFormProcessing implements MaternityFormProcessingTask<List<Event>> {

    @Override
    public List<Event> processMaternityForm(@NonNull String jsonString, @Nullable Intent data) throws JSONException {
        ArrayList<Event> eventList = new ArrayList<>();
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONArray fieldsArray = MaternityUtils.generateFieldsFromJsonForm(jsonFormObject);
        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        JSONObject metadata = jsonFormObject.getJSONObject(METADATA);

        String baseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.ENTITY_TABLE);
        Event closeMaternityEvent = JsonFormUtils.createEvent(fieldsArray, metadata, formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_CLOSE, entityTable);
        MaternityJsonFormUtils.tagSyncMetadata(closeMaternityEvent);
        eventList.add(closeMaternityEvent);

        processWomanDiedEvent(fieldsArray, closeMaternityEvent);

        return eventList;
    }


    protected void processWomanDiedEvent(JSONArray fieldsArray, Event event) throws JSONException {
        if ("woman_died".equals(getFieldValue(fieldsArray, "maternity_close_reason"))) {
            event.setEventType(MaternityConstants.EventType.DEATH);
            createDeathEventObject(event, fieldsArray);
        }
    }

    private void createDeathEventObject(Event event, JSONArray fieldsArray) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

        EventClientRepository db = MaternityLibrary.getInstance().eventClientRepository();

        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        String dateOfDeath = JsonFormUtils.getFieldValue(fieldsArray, "date_of_death");
        client.put(MaternityConstants.JSON_FORM_KEY.DEATH_DATE, StringUtils.isNotBlank(dateOfDeath) ? dateOfDeath : MaternityUtils.getTodaysDate());
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(MaternityConstants.JSON_FORM_KEY.DEATH_DATE_APPROX, false);

        db.addorUpdateClient(event.getBaseEntityId(), client);

        db.addEvent(event.getBaseEntityId(), eventJson);

        Event updateClientDetailsEvent = (Event) new Event().withBaseEntityId(event.getBaseEntityId())
                .withEventDate(DateTime.now().toDate()).withEventType(MaternityUtils.metadata().getUpdateEventType()).withLocationId(event.getLocationId())
                .withProviderId(event.getLocationId()).withEntityType(event.getEntityType())
                .withFormSubmissionId(generateRandomUUIDString()).withDateCreated(new Date());

        JSONObject eventJsonUpdateClientEvent = new JSONObject(JsonFormUtils.gson.toJson(updateClientDetailsEvent));

        db.addEvent(event.getBaseEntityId(), eventJsonUpdateClientEvent);
    }
}
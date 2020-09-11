package org.smartregister.maternity.configuration;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.maternity.utils.MaternityJsonFormUtils.METADATA;

public class MaternityMedicInfoFormProcessingTask implements MaternityFormProcessingTask<List<Event>> {

    @Override
    public List<Event> processMaternityForm(@NonNull String jsonString, @Nullable Intent data) throws JSONException {
        return processMaternityMedicInfoForm(jsonString, data);
    }

    private List<Event> processMaternityMedicInfoForm(@NonNull String jsonString, @Nullable Intent data) throws JSONException {
        List<Event> eventList = new ArrayList<>();

        JSONObject jsonFormObject = new JSONObject(jsonString);

        String baseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);

        JSONArray fieldsArray = MaternityUtils.generateFieldsFromJsonForm(jsonFormObject);

        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        Event maternityMedicInfoEvent = MaternityJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_MEDIC_INFO, "");
        MaternityJsonFormUtils.tagSyncMetadata(maternityMedicInfoEvent);
        eventList.add(maternityMedicInfoEvent);

        return eventList;

    }
}

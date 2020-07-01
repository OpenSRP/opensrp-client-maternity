package org.smartregister.maternity.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;

import java.util.HashMap;

public class MaternityProfileActivityModel {
    public JSONObject getFormAsJson(String formName, String caseId, String locationId, HashMap<String, String> injectedValues) throws JSONException {
        JSONObject form = MaternityUtils.getJsonFormToJsonObject(formName);
        if (form != null) {
            return MaternityJsonFormUtils.getFormAsJson(form, formName, caseId, locationId, injectedValues);
        }
        return null;
    }
}

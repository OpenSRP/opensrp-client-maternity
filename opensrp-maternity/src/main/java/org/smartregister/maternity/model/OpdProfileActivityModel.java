package org.smartregister.maternity.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.maternity.utils.OpdJsonFormUtils;
import org.smartregister.maternity.utils.OpdUtils;

import java.util.HashMap;

public class OpdProfileActivityModel {
    public JSONObject getFormAsJson(String formName, String caseId, String locationId, HashMap<String, String> injectedValues) throws JSONException {
        JSONObject form = OpdUtils.getJsonFormToJsonObject(formName);
        if (form != null) {
            return OpdJsonFormUtils.getFormAsJson(form, formName, caseId, locationId, injectedValues);
        }
        return null;
    }
}

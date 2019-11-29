package org.smartregister.opd.widgets;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.utils.MultiSelectListUtils;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.domain.Setting;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.utils.OpdConstants;

import java.util.List;

import timber.log.Timber;

public class OpdMultiSelectList extends MultiSelectListFactory {

    @Override
    public List<MultiSelectItem> fetchData() {
        Setting setting = OpdLibrary.getInstance().context().allSettings().getSetting(OpdConstants.SettingsConfig.OPD_DISEASE_CODES);
        try {
            JSONObject jsonValObject = setting != null ? new JSONObject(setting.getValue()) : null;
            if (jsonValObject != null) {
                JSONArray jsonOptionsArray = jsonValObject.optJSONArray(AllConstants.SETTINGS);
                if (jsonOptionsArray != null) {
                    JSONArray jsonValuesArray = jsonOptionsArray.optJSONObject(0)
                            .optJSONArray(JsonFormConstants.VALUES);
                    if (jsonValuesArray != null) {
                        return MultiSelectListUtils.processOptionsJsonArray(jsonValuesArray);
                    }
                }
            }
            return null;
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
    }
}

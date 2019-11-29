package org.smartregister.opd.model;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterActivityModel implements OpdRegisterActivityContract.Model {

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
        }
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
        }
    }

    @Override
    public void saveLanguage(String language) {
    }

    @Nullable
    @Override
    public String getLocationId(@Nullable String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Nullable
    @Override
    public List<OpdEventClient> processRegistration(String jsonString, FormTag formTag) {
        List<OpdEventClient> opdEventClientList = new ArrayList<>();
        OpdEventClient opdEventClient = OpdJsonFormUtils.processOpdDetailsForm(jsonString, formTag);

        if (opdEventClient == null) {
            return null;
        }

        opdEventClientList.add(opdEventClient);
        return opdEventClientList;
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws JSONException {
        return getFormAsJson(formName, entityId, currentLocationId, null);
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId,
                             String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException {
        JSONObject form = OpdUtils.getJsonFormToJsonObject(formName);
        if (form != null) {
            return OpdJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, injectedValues);
        }
        return null;
    }

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }

}

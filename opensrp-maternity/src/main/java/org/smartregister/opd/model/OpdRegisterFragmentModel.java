package org.smartregister.opd.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.opd.contract.OpdRegisterFragmentContract;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterFragmentModel implements OpdRegisterFragmentContract.Model {

    @Override
    public String getFilterText(List<Field> list, String filterTitle) {
        List<Field> filterList = list;
        if (filterList == null) {
            filterList = new ArrayList<>();
        }

        String filter = filterTitle;
        if (filter == null) {
            filter = "";
        }
        return "<font color=#727272>" + filter + "</font> <font color=#f0ab41>(" + filterList.size() + ")</font>";
    }

    @Override
    public String getSortText(Field sortField) {
        String sortText = "";
        if (sortField != null) {
            if (StringUtils.isNotBlank(sortField.getDisplayName())) {
                sortText = "(Sort: " + sortField.getDisplayName() + ")";
            } else if (StringUtils.isNotBlank(sortField.getDbAlias())) {
                sortText = "(Sort: " + sortField.getDbAlias() + ")";
            }
        }
        return sortText;
    }

    @Override
    public JSONArray getJsonArray(Response<String> response) {
        try {
            if (response.status().equals(ResponseStatus.success)) {
                return new JSONArray(response.payload());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

}
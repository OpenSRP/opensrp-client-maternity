package org.smartregister.maternity.contract;


import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */


public interface MaternityRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter();

        Presenter presenter();

        @Nullable
        String getDueOnlyText();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        String getDueFilterCondition();

    }

    interface Model {

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

        JSONArray getJsonArray(Response<String> response);

    }
}

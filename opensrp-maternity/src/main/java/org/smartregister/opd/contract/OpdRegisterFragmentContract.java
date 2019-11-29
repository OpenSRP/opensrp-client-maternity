package org.smartregister.opd.contract;


import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */


public interface OpdRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter();

        Presenter presenter();

        @NonNull
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

package org.smartregister.maternity.sample.fragment;


import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.smartregister.maternity.adapter.ClientLookUpListAdapter;
import org.smartregister.maternity.fragment.BaseMaternityFormFragment;


public class MaternityFormFragment extends BaseMaternityFormFragment implements ClientLookUpListAdapter.ClickListener {

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    public static MaternityFormFragment getFormFragment(String stepName) {
        MaternityFormFragment jsonFormFragment = new MaternityFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

}

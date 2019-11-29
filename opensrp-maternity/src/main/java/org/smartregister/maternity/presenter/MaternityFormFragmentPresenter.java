package org.smartregister.maternity.presenter;

import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.maternity.fragment.BaseMaternityFormFragment;
import org.smartregister.maternity.utils.MaternityConstants;

public class MaternityFormFragmentPresenter extends JsonWizardFormFragmentPresenter {

    public MaternityFormFragmentPresenter(BaseMaternityFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    protected boolean moveToNextWizardStep() {
        if (!TextUtils.isEmpty(mStepDetails.optString(JsonFormConstants.NEXT))) {
            JsonFormFragment next = BaseMaternityFormFragment.getFormFragment(mStepDetails.optString(MaternityConstants.JSON_FORM_EXTRA.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
            return true;
        }
        return false;
    }
}

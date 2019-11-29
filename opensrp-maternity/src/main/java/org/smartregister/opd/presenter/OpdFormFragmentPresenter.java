package org.smartregister.opd.presenter;

import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.utils.OpdConstants;

public class OpdFormFragmentPresenter extends JsonWizardFormFragmentPresenter {

    public OpdFormFragmentPresenter(BaseOpdFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    protected boolean moveToNextWizardStep() {
        if (!TextUtils.isEmpty(mStepDetails.optString(JsonFormConstants.NEXT))) {
            JsonFormFragment next = BaseOpdFormFragment.getFormFragment(mStepDetails.optString(OpdConstants.JSON_FORM_EXTRA.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
            return true;
        }
        return false;
    }
}

package org.smartregister.maternity.presenter;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojos.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class TestMaternityRegisterActivityPresenter extends BaseMaternityRegisterActivityPresenter {

    public TestMaternityRegisterActivityPresenter(MaternityRegisterActivityContract.View view, MaternityRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {

    }

    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        // Do nothing
    }
}

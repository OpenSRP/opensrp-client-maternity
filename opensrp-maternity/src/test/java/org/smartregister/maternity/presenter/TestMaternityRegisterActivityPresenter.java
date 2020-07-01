package org.smartregister.maternity.presenter;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojo.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class TestMaternityRegisterActivityPresenter extends BaseMaternityRegisterActivityPresenter {

    public TestMaternityRegisterActivityPresenter(MaternityRegisterActivityContract.View view, MaternityRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void onNoUniqueId() {
        // Do nothing
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // Do nothing
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        // Do nothing
    }

    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        // Do nothing
    }
}

package org.smartregister.maternity.presenter;

import androidx.annotation.NonNull;

import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojo.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterActivityPresenter extends BaseMaternityRegisterActivityPresenter {


    public MaternityRegisterActivityPresenter(MaternityRegisterActivityContract.View view, MaternityRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        // Do nothing
    }

    @Override
    public void onNoUniqueId() {
        // Do nothing
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        // Do nothing
    }
}

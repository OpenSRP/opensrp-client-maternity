package org.smartregister.opd.presenter;

import android.support.annotation.NonNull;

import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-24
 */

public class OpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter {


    public OpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
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

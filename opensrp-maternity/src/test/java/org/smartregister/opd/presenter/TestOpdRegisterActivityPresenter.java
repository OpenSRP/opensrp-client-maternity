package org.smartregister.opd.presenter;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.RegisterParams;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class TestOpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter {

    public TestOpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
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

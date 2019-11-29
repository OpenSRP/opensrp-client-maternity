package org.smartregister.maternity.sample.fragment;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.OpdLibrary;
import org.smartregister.maternity.fragment.BaseOpdRegisterFragment;
import org.smartregister.maternity.pojos.OpdMetadata;
import org.smartregister.maternity.sample.activity.OpdRegisterActivity;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class OpdRegisterFragment extends BaseOpdRegisterFragment {

    @Override
    protected void startRegistration() {
        OpdMetadata opdMetadata = OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
        if (getActivity() instanceof  OpdRegisterActivity && opdMetadata != null) {
            ((OpdRegisterActivity) getActivity()).startFormActivity(opdMetadata.getOpdRegistrationFormName(), null, null);
        }
    }

    @Override
    protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        // Do nothing
        Timber.i("Client Action button was clicked on OPD Register for client: %s", new Gson().toJson(commonPersonObjectClient));
    }

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        // Do nothing
        Timber.i("Client was clicked on OPD Register: %s", new Gson().toJson(commonPersonObjectClient));
    }
}

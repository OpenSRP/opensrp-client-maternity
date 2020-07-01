package org.smartregister.maternity.configuration;

import android.content.Context;
import android.support.annotation.NonNull;

import org.smartregister.commonregistry.CommonPersonObjectClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-20
 */

public interface MaternityRegisterSwitcher {

    void switchFromMaternityRegister(@NonNull CommonPersonObjectClient client, @NonNull Context context);

    boolean showRegisterSwitcher(@NonNull CommonPersonObjectClient client);
}

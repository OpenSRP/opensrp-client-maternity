package org.smartregister.opd.configuration;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class BaseOpdRegisterRowOptions implements OpdRegisterRowOptions<OpdRegisterViewHolder> {

    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull OpdRegisterViewHolder opdRegisterViewHolder) {
        // Do nothing
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }

    @Nullable
    @Override
    public OpdRegisterViewHolder createCustomViewHolder(@NonNull View itemView) {
        return null;
    }

    @Override
    public boolean useCustomViewLayout() {
        return false;
    }

    @Override
    public int getCustomViewLayoutId() {
        return 0;
    }
}

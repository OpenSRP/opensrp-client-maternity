package org.smartregister.maternity.configuration;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.holders.MaternityRegisterViewHolder;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BaseMaternityRegisterRowOptions implements MaternityRegisterRowOptions<MaternityRegisterViewHolder> {

    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull MaternityRegisterViewHolder maternityRegisterViewHolder) {
        // Do nothing
        MaternityUtils.setActionButtonStatus(maternityRegisterViewHolder.dueButton, commonPersonObjectClient);
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }

    @Nullable
    @Override
    public MaternityRegisterViewHolder createCustomViewHolder(@NonNull View itemView) {
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

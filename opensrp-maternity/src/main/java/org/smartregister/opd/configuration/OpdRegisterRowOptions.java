package org.smartregister.opd.configuration;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public interface OpdRegisterRowOptions<T extends OpdRegisterViewHolder> {

    boolean isDefaultPopulatePatientColumn();

    /**
     * You should set all the data that should be displayed for each client column here. For this use
     * the #opdRegisterViewHolder passed and in case you are using a custom one you can just cast it to
     * whatever you provided in {@link OpdRegisterRowOptions#createCustomViewHolder}
     *
     * @param cursor                   cursor object on the current row
     * @param commonPersonObjectClient Contains the column maps for the current user
     * @param smartRegisterClient
     * @param opdRegisterViewHolder    The recycler view holder which holds the required views
     */
    void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull OpdRegisterViewHolder opdRegisterViewHolder);

    boolean isCustomViewHolder();

    @Nullable
    T createCustomViewHolder(@NonNull View itemView);

    boolean useCustomViewLayout();

    @LayoutRes
    int getCustomViewLayoutId();

}

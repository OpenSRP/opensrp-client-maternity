package org.smartregister.maternity.activity;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.fragment.BaseMaternityRegisterFragment;
import org.smartregister.maternity.model.MaternityRegisterActivityModel;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public abstract class BaseMaternityRegisterActivity extends BaseRegisterActivity implements MaternityRegisterActivityContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerBottomNavigation() {
        try {
            View bottomNavGeneralView = findViewById(org.smartregister.R.id.bottom_navigation);
            if (bottomNavGeneralView instanceof BottomNavigationView) {
                BottomNavigationView bottomNavigationView = (BottomNavigationView) bottomNavGeneralView;
                if (!MaternityLibrary.getInstance().getMaternityConfiguration().isBottomNavigationEnabled()) {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        } catch (NoSuchFieldError e) {
            // This error occurs because the ID cannot be found on some client applications because the layout
            // has been overriden
            Timber.e(e);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = createPresenter(this, createActivityModel());
    }

    abstract protected BaseMaternityRegisterActivityPresenter createPresenter(@NonNull MaternityRegisterActivityContract.View view, @NonNull MaternityRegisterActivityContract.Model model);

    protected MaternityRegisterActivityContract.Model createActivityModel() {
        return new MaternityRegisterActivityModel();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, BaseMaternityRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public MaternityRegisterActivityContract.Presenter presenter() {
        return (MaternityRegisterActivityContract.Presenter) presenter;
    }

    @Override
    public void startRegistration() {
        //do nothing
    }

    @Override
    public void startFormActivityFromFormName(@NonNull String formName, @Nullable String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (mBaseFragment instanceof BaseMaternityRegisterFragment) {
            String locationId = MaternityUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, injectedFieldValues, entityTable);
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivityFromFormJson(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> intentData) {
        Intent intent = MaternityUtils.buildFormActivityIntent(jsonForm, intentData, this);
        if (intent != null) {
            startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
        } else {
            Timber.e(new Exception(), "FormActivity cannot be started because MaternityMetadata is NULL");
        }
    }
}
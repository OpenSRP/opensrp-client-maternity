package org.smartregister.maternity.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.fragment.BaseMaternityRegisterFragment;
import org.smartregister.maternity.model.MaternityRegisterActivityModel;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Iterator;
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
    public void startFormActivity(String formName, String entityId, String metaData) {
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
    public void startFormActivityFromFormJson(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData) {
        MaternityMetadata maternityMetadata = MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityMetadata();
        if (maternityMetadata != null) {
            Intent intent = new Intent(this, maternityMetadata.getMaternityFormActivity());
            intent.putExtra(MaternityConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
            Form form = new Form();
            form.setWizard(false);
            form.setName("");

            for (Iterator<String> objectKeys = jsonForm.keys(); objectKeys.hasNext();) {
                String key = objectKeys.next();
                if (!TextUtils.isEmpty(key) && key.contains("step") && !"step1".equalsIgnoreCase(key)) {
                    String encounterType = jsonForm.optString(MaternityConstants.JSON_FORM_KEY.ENCOUNTER_TYPE);

                    form.setName(encounterType);
                    form.setWizard(true);
                    break;
                }
            }

            form.setHideSaveLabel(true);
            form.setPreviousLabel("");
            form.setNextLabel("");
            form.setHideNextButton(false);
            form.setHidePreviousButton(false);

            if (MaternityConstants.EventType.MATERNITY_OUTCOME.equals(jsonForm.optString(MaternityConstants.JSON_FORM_KEY.ENCOUNTER_TYPE))) {
                form.setSaveLabel(getString(R.string.submit_and_close_maternity));
            }

            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            if (parcelableData != null) {
                for (String intentKey : parcelableData.keySet()) {
                    intent.putExtra(intentKey, parcelableData.get(intentKey));
                }
            }

            startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
        } else {
            Timber.e(new Exception(), "FormActivity cannot be started because MaternityMetadata is NULL");
        }
    }
}
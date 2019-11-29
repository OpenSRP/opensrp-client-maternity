package org.smartregister.opd.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public abstract class BaseOpdRegisterActivity extends BaseRegisterActivity implements OpdRegisterActivityContract.View {

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
                if (!OpdLibrary.getInstance().getOpdConfiguration().isBottomNavigationEnabled()) {
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

    abstract protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model);

    protected OpdRegisterActivityContract.Model createActivityModel() {
        return new OpdRegisterActivityModel();
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
        Intent intent = new Intent(this, BaseOpdRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public OpdRegisterActivityContract.Presenter presenter() {
        return (OpdRegisterActivityContract.Presenter) presenter;
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
    public void startFormActivity(String formName, String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (mBaseFragment instanceof BaseOpdRegisterFragment) {
            String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, injectedFieldValues, entityTable);
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData) {
        OpdMetadata opdMetadata = OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
        if (opdMetadata != null) {
            Intent intent = new Intent(this, opdMetadata.getOpdFormActivity());
            intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
            Form form = new Form();
            form.setWizard(false);
            form.setName("");

            String encounterType = jsonForm.optString(OpdJsonFormUtils.ENCOUNTER_TYPE);

            if (encounterType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
                form.setName(OpdConstants.EventType.DIAGNOSIS_AND_TREAT);
                form.setWizard(true);
            }

            form.setHideSaveLabel(true);
            form.setPreviousLabel("");
            form.setNextLabel("");
            form.setHideNextButton(false);
            form.setHidePreviousButton(false);

            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            if (parcelableData != null) {
                for (String intentKey : parcelableData.keySet()) {
                    intent.putExtra(intentKey, parcelableData.get(intentKey));
                }
            }

            startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
        } else {
            Timber.e(new Exception(), "FormActivity cannot be started because OpdMetadata is NULL");
        }
    }
}
package org.smartregister.maternity.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityRegisterActivity;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.fragment.BaseMaternityRegisterFragment;
import org.smartregister.maternity.model.MaternityRegisterActivityModel;
import org.smartregister.maternity.pojos.RegisterParams;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.sample.fragment.MaternityRegisterFragment;
import org.smartregister.maternity.sample.presenter.MaternityRegisterActivityPresenter;
import org.smartregister.maternity.sample.utils.SampleConstants;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterActivity extends BaseMaternityRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BaseMaternityRegisterActivityPresenter createPresenter(@NonNull MaternityRegisterActivityContract.View view, @NonNull MaternityRegisterActivityContract.Model model) {
        return new MaternityRegisterActivityPresenter(view, model);
    }

    @Override
    protected void initializePresenter() {
        presenter = new MaternityRegisterActivityPresenter(this, new MaternityRegisterActivityModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new MaternityRegisterFragment();
    }


    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaternityJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(MaternityJsonFormUtils.ENCOUNTER_TYPE).equals(MaternityUtils.metadata().getRegisterEventType())) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.context().allSharedPreferences()));

                    // showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public void startFormActivity(@NonNull String formName, @Nullable String entityId, @Nullable String metaData) {
        if (mBaseFragment instanceof BaseMaternityRegisterFragment) {
            String locationId = MaternityUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, null, null);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, MaternityLibrary.getInstance().getMaternityConfiguration().getOpdMetadata().getOpdFormActivity());
        if (jsonForm.has(SampleConstants.KEY.ENCOUNTER_TYPE) && jsonForm.optString(SampleConstants.KEY.ENCOUNTER_TYPE).equals(
                SampleConstants.KEY.OPD_REGISTRATION)) {
//            MaternityJsonFormUtils.addRegLocHierarchyQuestions(jsonForm, GizConstants.KEY.REGISTRATION_HOME_ADDRESS, LocationHierarchy.ENTIRE_TREE);
        }

        intent.putExtra(MaternityConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
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
        //Do nothing
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

}
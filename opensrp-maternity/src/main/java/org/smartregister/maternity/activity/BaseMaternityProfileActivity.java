package org.smartregister.maternity.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.adapter.ViewPagerAdapter;
import org.smartregister.maternity.configuration.MaternityRegisterSwitcher;
import org.smartregister.maternity.contract.MaternityProfileActivityContract;
import org.smartregister.maternity.fragment.MaternityProfileOverviewFragment;
import org.smartregister.maternity.fragment.MaternityProfileVisitsFragment;
import org.smartregister.maternity.listener.OnSendActionToFragment;
import org.smartregister.maternity.listener.OngoingTaskCompleteListener;
import org.smartregister.maternity.pojos.OngoingTask;
import org.smartregister.maternity.pojos.RegisterParams;
import org.smartregister.maternity.presenter.MaternityProfileActivityPresenter;
import org.smartregister.maternity.utils.ConfigurationInstancesHelper;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class BaseMaternityProfileActivity extends BaseProfileActivity implements MaternityProfileActivityContract.View {

    private TextView nameView;
    private TextView ageView;
    private TextView genderView;
    private TextView ancIdView;
    private ImageView imageView;
    private String baseEntityId;
    private OnSendActionToFragment sendActionListenerForProfileOverview;
    private OnSendActionToFragment sendActionListenerToVisitsFragment;

    private CommonPersonObjectClient commonPersonObjectClient;
    private Button switchRegBtn;
    private OngoingTask ongoingTask;

    @Override
    protected void initializePresenter() {
        presenter = new MaternityProfileActivityPresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        ageView = findViewById(R.id.textview_detail_two);
        genderView = findViewById(R.id.textview_detail_three);
        ancIdView = findViewById(R.id.textview_detail_one);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);
        switchRegBtn = findViewById(R.id.btn_maternityActivityBaseProfile_switchRegView);

        setTitle("");
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        MaternityProfileOverviewFragment profileOverviewFragment = MaternityProfileOverviewFragment.newInstance(this.getIntent().getExtras());
        setSendActionListenerForProfileOverview(profileOverviewFragment);

        MaternityProfileVisitsFragment profileVisitsFragment = MaternityProfileVisitsFragment.newInstance(this.getIntent().getExtras());
        setSendActionListenerToVisitsFragment(profileVisitsFragment);

        adapter.addFragment(profileOverviewFragment, this.getString(R.string.overview));
        adapter.addFragment(profileVisitsFragment, this.getString(R.string.anc_history));

        viewPager.setAdapter(adapter);
        return viewPager;
    }

    public void setSendActionListenerForProfileOverview(OnSendActionToFragment sendActionListenerForProfileOverview) {
        this.sendActionListenerForProfileOverview = sendActionListenerForProfileOverview;
    }

    public void setSendActionListenerToVisitsFragment(OnSendActionToFragment sendActionListenerToVisitsFragment) {
        this.sendActionListenerToVisitsFragment = sendActionListenerToVisitsFragment;
    }

    @Override
    protected void fetchProfileData() {
        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                .getSerializableExtra(MaternityConstants.IntentKey.CLIENT_OBJECT);
        ((MaternityProfileActivityPresenter) presenter).refreshProfileTopSection(commonPersonObjectClient.getColumnmaps());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.maternity_menu_item_close_client) {
            openMaternityCloseForm();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        MaternityProfileActivityContract.Presenter maternityProfilePresenter = (MaternityProfileActivityPresenter) presenter;

        if (!maternityProfilePresenter.hasOngoingTask()) {
            commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                    .getSerializableExtra(MaternityConstants.IntentKey.CLIENT_OBJECT);
            baseEntityId = commonPersonObjectClient.getCaseId();
            maternityProfilePresenter.refreshProfileTopSection(commonPersonObjectClient.getColumnmaps());

            // Enable switcher
            configureRegisterSwitcher();

            // Disable the registration info button if the client is not in Maternity
            if (commonPersonObjectClient != null) {
                String register_type = commonPersonObjectClient.getDetails().get(MaternityConstants.ColumnMapKey.REGISTER_TYPE);
                View view = findViewById(R.id.btn_profile_registration_info);
                view.setEnabled(MaternityConstants.RegisterType.MATERNITY.equalsIgnoreCase(register_type));
            }
        } else {
            maternityProfilePresenter.addOngoingTaskCompleteListener(new OngoingTaskCompleteListener() {
                @Override
                public void onTaskComplete(@NonNull OngoingTask ongoingTask) {
                    maternityProfilePresenter.removeOngoingTaskCompleteListener(this);
                    onResumption();
                }
            });
        }
    }

    private void configureRegisterSwitcher() {
        Class<? extends MaternityRegisterSwitcher> maternityRegisterSwitcherClass = MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityRegisterSwitcher();
        if (maternityRegisterSwitcherClass != null) {
            final MaternityRegisterSwitcher maternityRegisterSwitcher = ConfigurationInstancesHelper.newInstance(maternityRegisterSwitcherClass);

            switchRegBtn.setVisibility(maternityRegisterSwitcher.showRegisterSwitcher(commonPersonObjectClient) ? View.VISIBLE : View.GONE);
            switchRegBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maternityRegisterSwitcher.switchFromMaternityRegister(commonPersonObjectClient, BaseMaternityProfileActivity.this);
                }
            });
        } else {
            switchRegBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());

    }

    @Override
    public void setProfileName(@NonNull String fullName) {
        this.patientName = fullName;
        nameView.setText(fullName);
    }

    @Override
    public void setProfileID(@NonNull String registerId) {
        ancIdView.setText(String.format(getString(R.string.id_detail), registerId));
    }

    @Override
    public void setProfileAge(@NonNull String age) {
        genderView.setText(String.format(getString(R.string.age_details), age));

    }

    @Override
    public void setProfileGender(@NonNull String gender) {
        ageView.setText(gender);
    }

    @Override
    public void setProfileImage(@NonNull String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, R.drawable.gender_insensitive_avatar);
    }

    public OnSendActionToFragment getActionListenerForVisitFragment() {
        return sendActionListenerToVisitsFragment;
    }

    public OnSendActionToFragment getActionListenerForProfileOverview() {
        return sendActionListenerForProfileOverview;
    }

    @Override
    public void openMaternityOutcomeForm() {
        if (commonPersonObjectClient != null) {
            ((MaternityProfileActivityPresenter) presenter).startForm(MaternityConstants.Form.MATERNITY_OUTCOME, commonPersonObjectClient);
        }
    }

    @Override
    public void openMaternityCloseForm() {
        if (commonPersonObjectClient != null) {
            ((MaternityProfileActivityPresenter) presenter).startForm(MaternityConstants.Form.MATERNITY_CLOSE, commonPersonObjectClient);
        }
    }

    @Override
    public void startFormActivity(@NonNull JSONObject form, @NonNull HashMap<String, String> intentKeys) {
        Intent intent = MaternityUtils.buildFormActivityIntent(form, intentKeys, this);
        startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maternity_profile_activity, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaternityJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSON-Result : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(MaternityJsonFormUtils.ENCOUNTER_TYPE);

                OngoingTask ongoingTask = new OngoingTask();
                ongoingTask.setTaskType(OngoingTask.TaskType.PROCESS_FORM);
                ongoingTask.setTaskDetail(encounterType);

                addOngoingTask(ongoingTask);
                if (encounterType.equals(MaternityConstants.EventType.MATERNITY_OUTCOME)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    ((MaternityProfileActivityPresenter) this.presenter).saveOutcomeForm(encounterType, data);
                } else if (encounterType.equals(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION)) {
                    removeOngoingTask(ongoingTask);
                    showProgressDialog(R.string.saving_dialog_title);

                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(true);
                    registerParam.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.context().allSharedPreferences()));
                    showProgressDialog(R.string.saving_dialog_title);

                    ((MaternityProfileActivityPresenter) this.presenter).saveUpdateRegistrationForm(jsonString, registerParam);
                } else if (encounterType.equals(MaternityConstants.EventType.MATERNITY_CLOSE)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    ((MaternityProfileActivityPresenter) this.presenter).saveMaternityCloseForm(encounterType, data);
                } else {
                    removeOngoingTask(ongoingTask);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void addOngoingTask(@NonNull OngoingTask ongoingTask) {
        this.ongoingTask = ongoingTask;
        ((MaternityProfileActivityContract.Presenter) this.presenter).setOngoingTask(ongoingTask);
    }

    private void removeOngoingTask(@NonNull OngoingTask ongoingTask) {
        ((MaternityProfileActivityContract.Presenter) this.presenter).removeOngoingTask(ongoingTask);
        this.ongoingTask = null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.maternity_activity_base_profile);
    }

    @Override
    public void onClick(View view) {
        String register_type = commonPersonObjectClient.getDetails().get(MaternityConstants.ColumnMapKey.REGISTER_TYPE);
        if (view.getId() == R.id.btn_profile_registration_info) {
            if (MaternityConstants.RegisterType.MATERNITY.equalsIgnoreCase(register_type)) {
                if (presenter instanceof MaternityProfileActivityContract.Presenter) {
                    ((MaternityProfileActivityContract.Presenter) presenter).onUpdateRegistrationBtnCLicked(baseEntityId);
                }
            } else {
                showToast(getString(R.string.edit_maternity_registration_failure_message));
            }
        } else {
            super.onClick(view);
        }
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }

    @Nullable
    @Override
    public CommonPersonObjectClient getClient() {
        return commonPersonObjectClient;
    }

    @Override
    public void setClient(@NonNull CommonPersonObjectClient client) {
        this.commonPersonObjectClient = client;
    }

    @Override
    public void showMessage(@Nullable String text) {
        if (text != null) {
            Toast.makeText(this, text, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void closeView() {
        finish();
    }
}
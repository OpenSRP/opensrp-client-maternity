package org.smartregister.maternity.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.configuration.MaternityFormProcessingTask;
import org.smartregister.maternity.contract.MaternityProfileActivityContract;
import org.smartregister.maternity.interactor.MaternityProfileInteractor;
import org.smartregister.maternity.listener.MaternityEventActionCallBack;
import org.smartregister.maternity.listener.OngoingTaskCompleteListener;
import org.smartregister.maternity.model.MaternityProfileActivityModel;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.pojo.MaternityPartialForm;
import org.smartregister.maternity.pojo.OngoingTask;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.maternity.tasks.FetchRegistrationDataTask;
import org.smartregister.maternity.utils.ConfigurationInstancesHelper;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityEventUtils;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class MaternityProfileActivityPresenter implements MaternityProfileActivityContract.Presenter, MaternityProfileActivityContract.InteractorCallBack, MaternityEventActionCallBack {

    private WeakReference<MaternityProfileActivityContract.View> mProfileView;
    private MaternityProfileActivityContract.Interactor mProfileInteractor;

    private MaternityProfileActivityModel model;
    private JSONObject form = null;

    private OngoingTask ongoingTask = null;
    private ArrayList<OngoingTaskCompleteListener> ongoingTaskCompleteListeners = new ArrayList<>();

    public MaternityProfileActivityPresenter(MaternityProfileActivityContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new MaternityProfileInteractor(this);
        model = new MaternityProfileActivityModel();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Nullable
    @Override
    public MaternityProfileActivityContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        }

        return null;
    }

    @Override
    public void onRegistrationSaved(@Nullable CommonPersonObjectClient client, boolean isEdit) {
        CommonPersonObjectClient reassignableClient = client;
        if (getProfileView() != null) {
            getProfileView().hideProgressDialog();

            if (reassignableClient != null) {
                getProfileView().setClient(reassignableClient);
            } else {
                reassignableClient = getProfileView().getClient();
            }

            if (isEdit && reassignableClient != null) {
                refreshProfileTopSection(reassignableClient.getColumnmaps());
            }
        }
    }

    @Override
    public void onFetchedSavedPartialForm(@Nullable MaternityPartialForm savedPartialForm, @NonNull String caseId, @NonNull String entityTable) {
        try {
            if (savedPartialForm != null) {
                form = new JSONObject(savedPartialForm.getForm());
            }

            startFormActivity(form, caseId, entityTable);
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void startFormActivity(@Nullable JSONObject form, @NonNull String caseId, @NonNull String entityTable) {
        if (getProfileView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(MaternityConstants.IntentKey.BASE_ENTITY_ID, caseId);
            intentKeys.put(MaternityConstants.IntentKey.ENTITY_TABLE, entityTable);
            getProfileView().startFormActivity(form, intentKeys);
        }
    }

    @Override
    public void refreshProfileTopSection(@NonNull Map<String, String> client) {
        MaternityProfileActivityContract.View profileView = getProfileView();
        if (profileView != null) {
            profileView.setProfileName(client.get(MaternityDbConstants.KEY.FIRST_NAME) + " " + client.get(MaternityDbConstants.KEY.LAST_NAME));
            String translatedYearInitial = profileView.getString(R.string.abbrv_years);
            String dobString = client.get(MaternityConstants.KEY.DOB);

            if (dobString != null) {
                String clientAge = MaternityUtils.getClientAge(Utils.getDuration(dobString), translatedYearInitial);
                profileView.setProfileAge(clientAge);
            }

            profileView.setProfileID(Utils.getValue(client, MaternityDbConstants.KEY.REGISTER_ID, false));
            profileView.setProfileImage(Utils.getValue(client, MaternityDbConstants.KEY.ID, false));
            String gender = client.get(MaternityConstants.ClientMapKey.GENDER);

            if (gender != null) {
                profileView.setProfileGender(gender);
            }
        }
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientMap = commonPersonObjectClient.getColumnmaps();
        HashMap<String, String> injectedValues = new HashMap<>();

        injectedValues.put(MaternityConstants.JsonFormField.MOTHER_HIV_STATUS, clientMap.get("hiv_status_current"));
        String entityTable = clientMap.get(MaternityConstants.IntentKey.ENTITY_TABLE);

        startFormActivity(formName, commonPersonObjectClient.getCaseId(), entityTable, injectedValues);
    }

    public void startFormActivity(@NonNull String formName, @NonNull String caseId, @NonNull String entityTable, @Nullable HashMap<String, String> injectedValues) {
        if (mProfileView != null) {
            form = null;
            try {
                String locationId = MaternityUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                form = model.getFormAsJson(formName, caseId, locationId, injectedValues);

                // Fetch saved form & continue editing
                if (formName.equals(MaternityConstants.Form.MATERNITY_OUTCOME) || formName.equals(MaternityConstants.Form.MATERNITY_MEDIC_INFO)) {
                    mProfileInteractor.fetchSavedPartialForm(form.optString(JsonFormConstants.ENCOUNTER_TYPE), caseId, entityTable);
                } else {
                    startFormActivity(form, caseId, entityTable);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveOutcomeForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        MaternityEventUtils maternityEventUtils = new MaternityEventUtils();
        if (data != null) {
            jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(MaternityConstants.EventType.MATERNITY_OUTCOME)) {
            try {
                List<Event> maternityOutcomeAndCloseEvents = MaternityLibrary.getInstance().processMaternityOutcomeForm(eventType, jsonString, data);
                maternityEventUtils.saveEvents(maternityOutcomeAndCloseEvents, this);
                MaternityLibrary.getInstance().getAppExecutors().diskIO().execute(() -> MaternityLibrary.getInstance().getMaternityPartialFormRepository().delete(new MaternityPartialForm(MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID), eventType)));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveMedicInfoForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        MaternityEventUtils maternityEventUtils = new MaternityEventUtils();
        if (data != null) {
            jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(MaternityConstants.EventType.MATERNITY_MEDIC_INFO)) {
            try {
                List<Event> maternityMedicInfoEvents = MaternityLibrary.getInstance().processMaternityMedicInfoForm(eventType, jsonString, data);
                maternityEventUtils.saveEvents(maternityMedicInfoEvents, this);
                MaternityLibrary.getInstance().getAppExecutors().diskIO().execute(() -> MaternityLibrary.getInstance().getMaternityPartialFormRepository().delete(new MaternityPartialForm(MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID), eventType)));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveMaternityCloseForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        MaternityEventUtils maternityEventUtils = new MaternityEventUtils();
        if (data != null) {
            jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            Timber.e(new Exception("Maternity Close form JSON is null"));
            return;
        }

        if (eventType.equals(MaternityConstants.EventType.MATERNITY_CLOSE)) {
            try {
                MaternityFormProcessingTask<List<Event>> maternityFormProcessingTask = ConfigurationInstancesHelper.newInstance(MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityFormProcessingTasks(eventType));
                List<Event> maternityCloseEvents = maternityFormProcessingTask.processMaternityForm(jsonString, data);
                maternityEventUtils.saveEvents(maternityCloseEvents, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveUpdateRegistrationForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        try {
            if (registerParams.getFormTag() == null) {
                registerParams.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences()));
            }

            MaternityEventClient maternityEventClient = processRegistration(jsonString, registerParams.getFormTag());
            if (maternityEventClient == null) {
                return;
            }

            mProfileInteractor.saveRegistration(maternityEventClient, jsonString, registerParams, this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Nullable
    @Override
    public MaternityEventClient processRegistration(@NonNull String jsonString, @NonNull FormTag formTag) {
        MaternityEventClient maternityEventClient = MaternityJsonFormUtils.processMaternityRegistrationForm(jsonString, formTag);
        //TODO: Show the user this error toast
        //showErrorToast();

        if (maternityEventClient == null) {
            return null;
        }

        return maternityEventClient;
    }

    @Override
    public void onMaternityEventSaved() {
        MaternityProfileActivityContract.View view = getProfileView();
        if (view != null) {
            view.hideProgressDialog();

            if (getOngoingTask() != null) {
                view.showMessage(view.getString(R.string.maternity_client_close_message));
                view.closeView();

                removeOngoingTask(ongoingTask);
            }

        }
    }

    @Override
    public void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId) {
        if (getProfileView() != null) {
            Utils.startAsyncTask(new FetchRegistrationDataTask(new WeakReference<>(getProfileView().getContext()), jsonForm -> {
                MaternityMetadata metadata = MaternityUtils.metadata();

                MaternityProfileActivityContract.View profileView = getProfileView();
                if (profileView != null && metadata != null && jsonForm != null) {
                    Context context = profileView.getContext();
                    Intent intent = new Intent(context, metadata.getMaternityFormActivity());
                    Form formParam = new Form();
                    formParam.setWizard(false);
                    formParam.setHideSaveLabel(true);
                    formParam.setNextLabel("");
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm);
                    profileView.startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
                }
            }), new String[]{baseEntityId});
        }
    }

    @Override
    public boolean hasOngoingTask() {
        return ongoingTask != null;
    }

    @Override
    public OngoingTask getOngoingTask() {
        return ongoingTask;
    }

    @Override
    public boolean setOngoingTask(@NonNull OngoingTask ongoingTask) {
        if (this.ongoingTask == null) {
            this.ongoingTask = ongoingTask;
            return true;
        }

        return false;
    }

    @Override
    public boolean removeOngoingTask(@NonNull OngoingTask ongoingTask) {
        if (this.ongoingTask == ongoingTask) {
            for (OngoingTaskCompleteListener ongoingTaskCompleteListener : ongoingTaskCompleteListeners) {
                ongoingTaskCompleteListener.onTaskComplete(ongoingTask);
            }

            this.ongoingTask = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean addOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener) {
        return ongoingTaskCompleteListeners.add(ongoingTaskCompleteListener);
    }

    @Override
    public boolean removeOngoingTaskCompleteListener(@NonNull OngoingTaskCompleteListener ongoingTaskCompleteListener) {
        return ongoingTaskCompleteListeners.remove(ongoingTaskCompleteListener);
    }

    @Override
    public void openAncProfile() {

    }

    @Override
    public boolean hasAncProfile() {
        return false;
    }
}
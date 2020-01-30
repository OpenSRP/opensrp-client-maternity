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
import org.smartregister.maternity.contract.MaternityProfileActivityContract;
import org.smartregister.maternity.interactor.MaternityProfileInteractor;
import org.smartregister.maternity.listener.MaternityEventActionCallBack;
import org.smartregister.maternity.model.MaternityProfileActivityModel;
import org.smartregister.maternity.pojos.MaternityEventClient;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.maternity.pojos.MaternityOutcomeForm;
import org.smartregister.maternity.pojos.RegisterParams;
import org.smartregister.maternity.tasks.FetchRegistrationDataTask;
import org.smartregister.maternity.utils.AppExecutors;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityEventUtils;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
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
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable MaternityOutcomeForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable) {
        try {
            if (diagnosisAndTreatmentForm != null) {
                form = new JSONObject(diagnosisAndTreatmentForm.getForm());
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
        }
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientMap = commonPersonObjectClient.getColumnmaps();
        HashMap<String, String> injectedValues = new HashMap<>();
        injectedValues.put(MaternityConstants.JsonFormField.PATIENT_GENDER, clientMap.get(MaternityConstants.ClientMapKey.GENDER));
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
                if (formName.equals(MaternityConstants.Form.MATERNITY_OUTCOME)) {
                    mProfileInteractor.fetchSavedDiagnosisAndTreatmentForm(caseId, entityTable);
                } else {
                    startFormActivity(form, caseId, entityTable);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveVisitOrDiagnosisForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        MaternityEventUtils maternityEventUtils = new MaternityEventUtils(new AppExecutors());
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
        MaternityEventClient maternityEventClient = MaternityJsonFormUtils.processMaternityDetailsForm(jsonString, formTag);
        //TODO: Show the user this error toast
        //showErrorToast();

        if (maternityEventClient == null) {
            return null;
        }

        return maternityEventClient;
    }

    @Override
    public void onOpdEventSaved() {
        MaternityProfileActivityContract.View view = getProfileView();
        if (view != null) {
            view.getActionListenerForProfileOverview().onActionReceive();
            view.getActionListenerForVisitFragment().onActionReceive();
            view.hideProgressDialog();
        }
    }

    @Override
    public void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId) {
        if (getProfileView() != null) {
            Utils.startAsyncTask(new FetchRegistrationDataTask(new WeakReference<Context>(getProfileView().getContext()), new FetchRegistrationDataTask.OnTaskComplete() {
                @Override
                public void onSuccess(@Nullable String jsonForm) {
                    MaternityMetadata metadata = MaternityUtils.metadata();

                    MaternityProfileActivityContract.View profileView = getProfileView();
                    if (profileView != null && metadata != null && jsonForm != null) {
                        Context context = profileView.getContext();
                        Intent intent = new Intent(context, metadata.getOpdFormActivity());
                        Form formParam = new Form();
                        formParam.setWizard(false);
                        formParam.setHideSaveLabel(true);
                        formParam.setNextLabel("");
                        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
                        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm);
                        profileView.startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
                    }
                }
            }), new String[]{baseEntityId});
        }
    }
}
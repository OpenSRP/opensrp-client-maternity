package org.smartregister.opd.presenter;

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
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.interactor.OpdProfileInteractor;
import org.smartregister.opd.listener.OpdEventActionCallBack;
import org.smartregister.opd.model.OpdProfileActivityModel;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.tasks.FetchRegistrationDataTask;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdEventUtils;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileActivityPresenter implements OpdProfileActivityContract.Presenter, OpdProfileActivityContract.InteractorCallBack, OpdEventActionCallBack {

    private WeakReference<OpdProfileActivityContract.View> mProfileView;
    private OpdProfileActivityContract.Interactor mProfileInteractor;

    private OpdProfileActivityModel model;
    private JSONObject form = null;

    public OpdProfileActivityPresenter(OpdProfileActivityContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new OpdProfileInteractor(this);
        model = new OpdProfileActivityModel();
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
    public OpdProfileActivityContract.View getProfileView() {
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
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable) {
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
            intentKeys.put(OpdConstants.IntentKey.BASE_ENTITY_ID, caseId);
            intentKeys.put(OpdConstants.IntentKey.ENTITY_TABLE, entityTable);
            getProfileView().startFormActivity(form, intentKeys);
        }
    }

    @Override
    public void refreshProfileTopSection(@NonNull Map<String, String> client) {
        OpdProfileActivityContract.View profileView = getProfileView();
        if (profileView != null) {
            profileView.setProfileName(client.get(OpdDbConstants.KEY.FIRST_NAME) + " " + client.get(OpdDbConstants.KEY.LAST_NAME));
                String translatedYearInitial = profileView.getString(R.string.abbrv_years);
            String dobString = client.get(OpdConstants.KEY.DOB);

            if (dobString != null) {
                String clientAge = OpdUtils.getClientAge(Utils.getDuration(dobString), translatedYearInitial);
                profileView.setProfileAge(clientAge);
            }

            try {
                profileView.setProfileGender(Utils.getValue(client, OpdDbConstants.KEY.GENDER, true));
            } catch (Exception e) {
                Timber.e(e);
                profileView.setProfileGender("");
            }

            profileView.setProfileID(Utils.getValue(client, OpdDbConstants.KEY.REGISTER_ID, false));
            profileView.setProfileImage(Utils.getValue(client, OpdDbConstants.KEY.ID, false));
        }
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientMap = commonPersonObjectClient.getColumnmaps();
        HashMap<String, String> injectedValues = new HashMap<>();
        injectedValues.put(OpdConstants.JsonFormField.PATIENT_GENDER, clientMap.get(OpdConstants.ClientMapKey.GENDER));
        String entityTable = clientMap.get(OpdConstants.IntentKey.ENTITY_TABLE);

        startFormActivity(formName, commonPersonObjectClient.getCaseId(), entityTable, injectedValues);
    }

    public void startFormActivity(@NonNull String formName, @NonNull String caseId, @NonNull String entityTable, @Nullable HashMap<String, String> injectedValues) {
        if (mProfileView != null) {
            form = null;
            try {
                String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                form = model.getFormAsJson(formName, caseId, locationId, injectedValues);

                // Fetch saved form & continue editing
                if (formName.equals(OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT)) {
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
        OpdEventUtils opdEventUtils = new OpdEventUtils(new AppExecutors());
        if (data != null) {
            jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(OpdConstants.EventType.CHECK_IN)) {
            try {
                Event opdVisitEvent = OpdLibrary.getInstance().processOpdCheckInForm(eventType, jsonString, data);
                opdEventUtils.saveEvents(Collections.singletonList(opdVisitEvent), this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        } else if (eventType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
            try {
                List<Event> opdDiagnosisAndTreatment = OpdLibrary.getInstance().processOpdDiagnosisAndTreatmentForm(jsonString, data);
                opdEventUtils.saveEvents(opdDiagnosisAndTreatment, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveUpdateRegistrationForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        try {
            if (registerParams.getFormTag() == null) {
                registerParams.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences()));
            }

            OpdEventClient opdEventClient = processRegistration(jsonString, registerParams.getFormTag());
            if (opdEventClient == null) {
                return;
            }

            mProfileInteractor.saveRegistration(opdEventClient, jsonString, registerParams, this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Nullable
    @Override
    public OpdEventClient processRegistration(@NonNull String jsonString, @NonNull FormTag formTag) {
        OpdEventClient opdEventClient = OpdJsonFormUtils.processOpdDetailsForm(jsonString, formTag);
        //TODO: Show the user this error toast
        //showErrorToast();

        if (opdEventClient == null) {
            return null;
        }

        return opdEventClient;
    }

    @Override
    public void onOpdEventSaved() {
        OpdProfileActivityContract.View view = getProfileView();
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
                    OpdMetadata metadata = OpdUtils.metadata();

                    OpdProfileActivityContract.View profileView = getProfileView();
                    if (profileView != null && metadata != null && jsonForm != null) {
                        Context context = profileView.getContext();
                        Intent intent = new Intent(context, metadata.getOpdFormActivity());
                        Form formParam = new Form();
                        formParam.setWizard(false);
                        formParam.setHideSaveLabel(true);
                        formParam.setNextLabel("");
                        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
                        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm);
                        profileView.startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
                    }
                }
            }), new String[]{baseEntityId});
        }
    }
}
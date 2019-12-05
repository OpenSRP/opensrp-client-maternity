package org.smartregister.maternity.presenter;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.interactor.BaseMaternityRegisterActivityInteractor;
import org.smartregister.maternity.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.maternity.utils.MaternityConstants;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public abstract class BaseMaternityRegisterActivityPresenter implements MaternityRegisterActivityContract.Presenter, MaternityRegisterActivityContract.InteractorCallBack {

    protected WeakReference<MaternityRegisterActivityContract.View> viewReference;
    protected MaternityRegisterActivityContract.Interactor interactor;
    protected MaternityRegisterActivityContract.Model model;
    private JSONObject form;

    public BaseMaternityRegisterActivityPresenter(@NonNull MaternityRegisterActivityContract.View view, @NonNull MaternityRegisterActivityContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = createInteractor();
        this.model = model;
    }

    @NonNull
    @Override
    public MaternityRegisterActivityContract.Interactor createInteractor() {
        return new BaseMaternityRegisterActivityInteractor();
    }

    public void setModel(MaternityRegisterActivityContract.Model model) {
        this.model = model;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy\
        if (!isChangingConfiguration) {
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null && getView() != null) {
            getView().updateInitialsText(initials);
        }
    }

    @Nullable
    private MaternityRegisterActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);

        if (getView() != null) {
            getView().displayToast(language + " selected");
        }
    }

    @Override
    public void saveVisitOrDiagnosisForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(MaternityConstants.EventType.CHECK_IN)) {
            try {
                Event opdVisitEvent = MaternityLibrary.getInstance().processOpdCheckInForm(eventType, jsonString, data);
                interactor.saveEvents(Collections.singletonList(opdVisitEvent), this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        } else if (eventType.equals(MaternityConstants.EventType.DIAGNOSIS_AND_TREAT)) {
            try {
                List<Event> opdDiagnosisAndTreatment = MaternityLibrary.getInstance().processOpdDiagnosisAndTreatmentForm(jsonString, data);
                interactor.saveEvents(opdDiagnosisAndTreatment, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void onEventSaved() {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    @Override
    public void startForm(@NonNull String formName, @Nullable String entityId, String metaData
            , @NonNull String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {

        // Todo: Refactor this method to only start the form and move the logic for getting a unique id to another method
        // We are also sure
        if (StringUtils.isBlank(entityId)) {
            //Todo: Check if this metadata is usually null OR can be null at
            Triple<String, String, String> triple = Triple.of(formName, metaData, locationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        form = null;
        try {
            form = model.getFormAsJson(formName, entityId, locationId, injectedFieldValues);
            // Todo: Enquire if we have to save a session of the outcome form to be continued later
            if (formName.equals(MaternityConstants.Form.OPD_DIAGNOSIS_AND_TREAT)) {
                interactor.fetchSavedDiagnosisAndTreatmentForm(entityId, entityTable, this);
                return;
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        // The form will be started directly for forms that do not have saved sessions
        startFormActivity(entityId, entityTable, form);
    }

    @Override
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm, @NonNull String caseId, @Nullable String entityTable) {
        try {
            if (diagnosisAndTreatmentForm != null) {
                form = new JSONObject(diagnosisAndTreatmentForm.getForm());
            }

            startFormActivity(caseId, entityTable, form);
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    private void startFormActivity(@NonNull String entityId, @Nullable String entityTable, @Nullable JSONObject form) {
        if (getView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(MaternityConstants.IntentKey.BASE_ENTITY_ID, entityId);
            intentKeys.put(MaternityConstants.IntentKey.ENTITY_TABLE, entityTable);

            getView().startFormActivityFromFormJson(form, intentKeys);
        }
    }

    @Override
    public void onUniqueIdFetched(@NonNull Triple<String, String, String> triple, @NonNull String entityId) {
        startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), null, null);
    }
}
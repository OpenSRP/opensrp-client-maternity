package org.smartregister.maternity.sample.presenter;

import android.support.annotation.NonNull;

import org.smartregister.domain.FetchStatus;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.sample.R;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterActivityPresenter extends BaseMaternityRegisterActivityPresenter implements MaternityRegisterActivityContract.InteractorCallBack {

    public MaternityRegisterActivityPresenter(MaternityRegisterActivityContract.View view, MaternityRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        if (registerParams.getFormTag() == null) {
            registerParams.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences()));
        }

        List<MaternityEventClient> maternityEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
        if (maternityEventClientList == null || maternityEventClientList.isEmpty()) {
            return;
        }

        registerParams.setEditMode(false);
        interactor.saveRegistration(maternityEventClientList, jsonString, registerParams, this);
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null) {
            getView().displayShortToast(R.string.no_unique_id);
        }
    }

    @NonNull
    @Override
    public MaternityRegisterActivityContract.Interactor createInteractor() {
        return new MaternityRegisterActivityInteractor();
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }
/*
    @Override
    public void onUniqueIdFetched(@NonNull Triple<String, String, String> triple, @NonNull String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), null, null);
        } catch (Exception e) {
            Timber.e(e);
            if (getView() != null) {
                getView().displayToast(R.string.error_unable_to_start_form);
            }
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
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    public void setModel(MaternityRegisterActivityContract.Model model) {
        this.model = model;
    }
    */
}

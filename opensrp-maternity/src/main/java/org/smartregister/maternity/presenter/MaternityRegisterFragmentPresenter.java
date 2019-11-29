package org.smartregister.maternity.presenter;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.maternity.contract.MaternityRegisterFragmentContract;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterFragmentPresenter implements MaternityRegisterFragmentContract.Presenter {

    private WeakReference<MaternityRegisterFragmentContract.View> viewReference;
    private MaternityRegisterFragmentContract.Model model;

    public MaternityRegisterFragmentPresenter(MaternityRegisterFragmentContract.View view, MaternityRegisterFragmentContract.Model model) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
    }

    @Override
    public void processViewConfigurations() {
        // Do nothing since we don't have process views
    }

    @Override
    public void initializeQueries(String mainCondition) {
        getView().initializeQueryParams("ec_client", null, null);
        getView().initializeAdapter();

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    protected MaternityRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {

            return null;
        }
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getDueFilterCondition() {
        return "DUE_ONLY";
    }

    public void setModel(MaternityRegisterFragmentContract.Model model) {
        this.model = model;
    }
}

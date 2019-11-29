package org.smartregister.maternity.presenter;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.maternity.contract.OpdRegisterFragmentContract;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterFragmentPresenter implements OpdRegisterFragmentContract.Presenter {

    private WeakReference<OpdRegisterFragmentContract.View> viewReference;
    private OpdRegisterFragmentContract.Model model;

    public OpdRegisterFragmentPresenter(OpdRegisterFragmentContract.View view, OpdRegisterFragmentContract.Model model) {
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

    protected OpdRegisterFragmentContract.View getView() {
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

    public void setModel(OpdRegisterFragmentContract.Model model) {
        this.model = model;
    }
}

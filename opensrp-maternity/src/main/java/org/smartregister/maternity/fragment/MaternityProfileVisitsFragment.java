package org.smartregister.maternity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rey.material.widget.TextView;

import org.apache.commons.lang3.NotImplementedException;
import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.R;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.contract.MaternityProfileVisitsFragmentContract;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.listener.OnSendActionToFragment;
import org.smartregister.maternity.presenter.MaternityProfileVisitsFragmentPresenter;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityProfileVisitsFragment extends BaseProfileFragment implements MaternityProfileVisitsFragmentContract.View, OnSendActionToFragment, View.OnClickListener {

    private MaternityProfileVisitsFragmentContract.Presenter presenter;
    private String baseEntityId;

    public static MaternityProfileVisitsFragment newInstance(@Nullable Bundle bundle) {
        Bundle args = bundle;
        MaternityProfileVisitsFragment fragment = new MaternityProfileVisitsFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new MaternityProfileVisitsFragmentPresenter(this);
    }

    @Override
    protected void onCreation() {
        initializePresenter();
        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(MaternityConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    protected void onResumption() {
        presenter.loadPageCounter(baseEntityId);
        presenter.loadVisits(baseEntityId, new MaternityProfileVisitsFragmentContract.Presenter.OnFinishedCallback() {
            @Override
            public void onFinished(@NonNull List<Object> ancVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                displayVisits(ancVisitSummaries, items);
            }
        });
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy(false);
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.maternity_fragment_profile_visits, container, false);
        Button btnAncHistory = fragmentView.findViewById(R.id.btn_anc_history);
        btnAncHistory.setOnClickListener(this);
        TextView txtAncHistory = fragmentView.findViewById(R.id.txt_no_anc_history);
        if (hasAncProfile()) {
            txtAncHistory.setVisibility(View.GONE);
            btnAncHistory.setVisibility(View.VISIBLE);
        } else {
            txtAncHistory.setVisibility(View.VISIBLE);
            btnAncHistory.setVisibility(View.GONE);
        }
        return fragmentView;
    }

    @Override
    public void onActionReceive() {
        onResumption();
    }

    @Override
    public void showPageCountText(@NonNull String pageCounterText) {
        throw new NotImplementedException("");
    }

    @Override
    public void showNextPageBtn(boolean show) {
        throw new NotImplementedException("");
    }

    @Override
    public void showPreviousPageBtn(boolean show) {
        throw new NotImplementedException("");
    }

    @Override
    public void displayVisits(@NonNull List<Object> ancVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        throw new NotImplementedException("");
    }

    @Nullable
    @Override
    public String getClientBaseEntityId() {
        return baseEntityId;
    }

    private boolean hasAncProfile() {
        return ((BaseMaternityProfileActivity) getActivity()).presenter().hasAncProfile();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_anc_history) {
            ((BaseMaternityProfileActivity) getActivity()).presenter().openAncProfile();
        }
    }
}

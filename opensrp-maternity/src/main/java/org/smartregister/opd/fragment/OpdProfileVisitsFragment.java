package org.smartregister.opd.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.OpdProfileVisitsAdapter;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.listener.OnSendActionToFragment;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.presenter.OpdProfileVisitsFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class OpdProfileVisitsFragment extends BaseProfileFragment implements OpdProfileVisitsFragmentContract.View, OnSendActionToFragment {

    private RecyclerView recyclerView;
    private OpdProfileVisitsFragmentContract.Presenter presenter;
    private String baseEntityId;
    private Button nextPageBtn;
    private Button previousPageBtn;
    private TextView pageCounter;

    public static OpdProfileVisitsFragment newInstance(@Nullable Bundle bundle) {
        Bundle args = bundle;
        OpdProfileVisitsFragment fragment = new OpdProfileVisitsFragment();
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
        presenter = new OpdProfileVisitsFragmentPresenter(this);
    }

    @Override
    protected void onCreation() {
        initializePresenter();
        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(OpdConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    protected void onResumption() {
        presenter.loadPageCounter(baseEntityId);
        presenter.loadVisits(baseEntityId, new OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback() {
            @Override
            public void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                displayVisits(opdVisitSummaries, items);
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
        View fragmentView = inflater.inflate(R.layout.opd_fragment_profile_visits, container, false);

        recyclerView = fragmentView.findViewById(R.id.rv_opdFragmentProfileVisit_recyclerView);
        nextPageBtn = fragmentView.findViewById(R.id.btn_opdFragmentProfileVisit_nextPageBtn);
        previousPageBtn = fragmentView.findViewById(R.id.btn_opdFragmentProfileVisit_previousPageBtn);
        pageCounter = fragmentView.findViewById(R.id.tv_opdFragmentProfileVisit_pageCounter);

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNextPageClicked();
            }
        });
        previousPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPreviousPageClicked();
            }
        });

        return fragmentView;
    }

    @Override
    public void onActionReceive() {
        onResumption();
    }

    @Override
    public void showPageCountText(@NonNull String pageCounterText) {
        this.pageCounter.setText(pageCounterText);
    }

    @Override
    public void showNextPageBtn(boolean show) {
        nextPageBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        nextPageBtn.setClickable(show);
    }

    @Override
    public void showPreviousPageBtn(boolean show) {
        previousPageBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        previousPageBtn.setClickable(show);
    }

    @Override
    public void displayVisits(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        if (getActivity() != null) {
            OpdProfileVisitsAdapter adapter = new OpdProfileVisitsAdapter(getActivity(), items);
            adapter.notifyDataSetChanged();

            // set up the RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
    }

    @Nullable
    @Override
    public String getClientBaseEntityId() {
        return baseEntityId;
    }
}

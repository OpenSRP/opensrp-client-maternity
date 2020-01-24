package org.smartregister.maternity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.R;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.adapter.MaternityProfileOverviewAdapter;
import org.smartregister.maternity.contract.MaternityProfileOverviewFragmentContract;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.listener.OnSendActionToFragment;
import org.smartregister.maternity.presenter.MaternityProfileOverviewFragmentPresenter;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.List;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class MaternityProfileOverviewFragment extends BaseProfileFragment implements MaternityProfileOverviewFragmentContract.View, OnSendActionToFragment {

    private String baseEntityId;
    private MaternityProfileOverviewFragmentContract.Presenter presenter;

    private LinearLayout opdCheckinSectionLayout;
    private Button checkInDiagnoseAndTreatBtn;
    private TextView opdCheckedInTv;

    public static MaternityProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        MaternityProfileOverviewFragment fragment = new MaternityProfileOverviewFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreation() {
        presenter = new MaternityProfileOverviewFragmentPresenter(this);

        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(MaternityConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                presenter.setClient(commonPersonObjectClient);
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    protected void onResumption() {
        if (baseEntityId != null) {
            presenter.loadOverviewFacts(baseEntityId, new MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback() {

                @Override
                public void onFinished(@Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal) {
                    if (getActivity() != null && facts != null && yamlConfigListGlobal != null) {
                        opdCheckedInTv.setText(R.string.opd);
                        showCheckInBtn();

                        MaternityProfileOverviewAdapter adapter = new MaternityProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
                        adapter.notifyDataSetChanged();
                        // set up the RecyclerView
                        RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);
                    }
                }

            });
        }
    }

    private void showCheckInBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInDiagnoseAndTreatBtn.setText(R.string.outcome);
            checkInDiagnoseAndTreatBtn.setBackgroundResource(R.drawable.check_in_btn_overview_bg);
            checkInDiagnoseAndTreatBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.check_in_btn_overview_text_color));
            checkInDiagnoseAndTreatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();

                    if (activity instanceof BaseMaternityProfileActivity) {
                        ((BaseMaternityProfileActivity) activity).openCheckInForm();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.maternity_fragment_profile_overview, container, false);

        opdCheckinSectionLayout = view.findViewById(R.id.ll_opdFragmentProfileOverview_checkinLayout);
        opdCheckedInTv = view.findViewById(R.id.tv_opdFragmentProfileOverview_checkedInTitle);
        checkInDiagnoseAndTreatBtn = view.findViewById(R.id.btn_opdFragmentProfileOverview_diagnoseAndTreat);

        return view;
    }

    @Override
    public void onActionReceive() {
        onResumption();
    }
}
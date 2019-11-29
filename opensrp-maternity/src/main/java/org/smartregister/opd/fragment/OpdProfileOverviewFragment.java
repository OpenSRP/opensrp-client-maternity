package org.smartregister.opd.fragment;

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
import org.smartregister.opd.R;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.adapter.OpdProfileOverviewAdapter;
import org.smartregister.opd.contract.OpdProfileOverviewFragmentContract;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.listener.OnSendActionToFragment;
import org.smartregister.opd.presenter.OpdProfileOverviewFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.List;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class OpdProfileOverviewFragment extends BaseProfileFragment implements OpdProfileOverviewFragmentContract.View, OnSendActionToFragment {

    private String baseEntityId;
    private OpdProfileOverviewFragmentContract.Presenter presenter;

    private LinearLayout opdCheckinSectionLayout;
    private Button checkInDiagnoseAndTreatBtn;
    private TextView opdCheckedInTv;

    public static OpdProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        OpdProfileOverviewFragment fragment = new OpdProfileOverviewFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreation() {
        presenter = new OpdProfileOverviewFragmentPresenter(this);

        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(OpdConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                presenter.setClient(commonPersonObjectClient);
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    protected void onResumption() {
        if (baseEntityId != null) {
            presenter.loadOverviewFacts(baseEntityId, new OpdProfileOverviewFragmentContract.Presenter.OnFinishedCallback() {

                @Override
                public void onFinished(@Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal) {
                    if (getActivity() != null && facts != null && yamlConfigListGlobal != null) {
                        Boolean isPendingDiagnoseAndTreat = facts.get(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT);
                        isPendingDiagnoseAndTreat = isPendingDiagnoseAndTreat == null ? Boolean.FALSE : isPendingDiagnoseAndTreat;

                        if (isPendingDiagnoseAndTreat) {
                            opdCheckedInTv.setText(R.string.opd_checked_in);
                            showDiagnoseAndTreatBtn();
                        } else {
                            opdCheckedInTv.setText(R.string.opd);
                            showCheckInBtn();
                        }

                        OpdProfileOverviewAdapter adapter = new OpdProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
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
            checkInDiagnoseAndTreatBtn.setText(R.string.check_in);
            checkInDiagnoseAndTreatBtn.setBackgroundResource(R.drawable.check_in_btn_overview_bg);
            checkInDiagnoseAndTreatBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.check_in_btn_overview_text_color));
            checkInDiagnoseAndTreatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();

                    if (activity instanceof BaseOpdProfileActivity) {
                        ((BaseOpdProfileActivity) activity).openCheckInForm();
                    }
                }
            });
        }
    }

    private void showDiagnoseAndTreatBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInDiagnoseAndTreatBtn.setText(R.string.diagnose_and_treat);
            checkInDiagnoseAndTreatBtn.setBackgroundResource(R.drawable.diagnose_treat_bg);
            checkInDiagnoseAndTreatBtn.setTextColor(getActivity().getResources().getColor(R.color.diagnose_treat_txt_color));
            checkInDiagnoseAndTreatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();

                    if (activity instanceof BaseOpdProfileActivity) {
                        ((BaseOpdProfileActivity) activity).openDiagnoseAndTreatForm();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.opd_fragment_profile_overview, container, false);

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
package org.smartregister.maternity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.List;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class MaternityProfileOverviewFragment extends BaseProfileFragment implements MaternityProfileOverviewFragmentContract.View, OnSendActionToFragment {

    private String baseEntityId;
    private MaternityProfileOverviewFragmentContract.Presenter presenter;

    private LinearLayout maternityOutcomeSectionLayout;
    private Button recordOutcomeBtn;
    private CommonPersonObjectClient commonPersonObjectClient;

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
            commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
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
                        showOutcomeBtn();

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

    private void showOutcomeBtn() {
        if (getActivity() != null) {
            updateActionButtonStatus(recordOutcomeBtn, commonPersonObjectClient);
            maternityOutcomeSectionLayout.setVisibility(View.VISIBLE);
            recordOutcomeBtn.setOnClickListener(v -> {
                Object buttonType = v.getTag(R.id.BUTTON_TYPE);

                if (buttonType != null) {
                    BaseMaternityProfileActivity profileActivity = (BaseMaternityProfileActivity) getActivity();
                    if (buttonType.equals(R.string.outcome)) {
                        profileActivity.openMaternityOutcomeForm();
                    } else if (buttonType.equals(R.string.complete_registration)) {
                        profileActivity.openMaternityMedicInfoForm();
                    }
                }
            });
        }
    }

    protected void updateActionButtonStatus(Button recordOutcomeBtn, CommonPersonObjectClient commonPersonObjectClient) {
        MaternityUtils.setActionButtonStatus(recordOutcomeBtn, commonPersonObjectClient);
        recordOutcomeBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, recordOutcomeBtn.getResources().getDimension(R.dimen.maternity_profile_action_button_text_size));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maternity_fragment_profile_overview, container, false);

        maternityOutcomeSectionLayout = view.findViewById(R.id.ll_maternityFragmentProfileOverview_outcomeLayout);
        recordOutcomeBtn = view.findViewById(R.id.btn_maternityFragmentProfileOverview_outcome);

        return view;
    }

    @Override
    public void onActionReceive() {
        onResumption();
    }

    @Override
    @Nullable
    public CommonPersonObjectClient getActivityClientMap() {
        if (getActivity() instanceof BaseMaternityProfileActivity) {
            return ((BaseMaternityProfileActivity) getActivity()).getClient();
        }

        return null;
    }
}
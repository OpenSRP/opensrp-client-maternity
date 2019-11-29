package org.smartregister.maternity.sample.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.fragment.BaseMaternityRegisterFragment;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.maternity.sample.R;
import org.smartregister.maternity.sample.activity.MaternityRegisterActivity;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterFragment extends BaseMaternityRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {/*
            SwitchCompat switchSelection = view.findViewById(R.id.switch_selection);
            if (switchSelection != null) {
                switchSelection.setText(getDueOnlyText());
                switchSelection.setOnClickListener(registerActionHandler);
            }*/

            View topLeftLayout = view.findViewById(R.id.top_left_layout);
            topLeftLayout.setVisibility(View.VISIBLE);

            ImageView addPatientBtn = view.findViewById(R.id.add_maternity_image_view);

            if (addPatientBtn != null) {
                addPatientBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRegistration();
                    }
                });
            }

            // Disable go-back on clicking the OPD Register title
            view.findViewById(R.id.title_layout).setOnClickListener(null);
        }

        return view;
    }

    @Override
    protected void startRegistration() {
        MaternityRegisterActivity opdRegisterActivity = (MaternityRegisterActivity) getActivity();
        MaternityMetadata maternityMetadata = MaternityLibrary.getInstance().getMaternityConfiguration().getOpdMetadata();

        if (maternityMetadata != null && opdRegisterActivity != null) {
            opdRegisterActivity.startFormActivity(maternityMetadata.getOpdRegistrationFormName()
                    , null
                    , null);
        }
    }

    @Override
    protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientColumnMaps = commonPersonObjectClient.getColumnmaps();

        MaternityRegisterActivity opdRegisterActivity = (MaternityRegisterActivity) getActivity();
        if (opdRegisterActivity != null && clientColumnMaps.containsKey(MaternityDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT)) {
            HashMap<String, String> injectedValues = new HashMap<String, String>();
            injectedValues.put("patient_gender", clientColumnMaps.get("gender"));

            String diagnoseSchedule = clientColumnMaps.get(MaternityDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT);
            String entityTable = clientColumnMaps.get(MaternityConstants.IntentKey.ENTITY_TABLE);

            boolean isDiagnoseScheduled = !TextUtils.isEmpty(diagnoseSchedule) && "1".equals(diagnoseSchedule);

            String strVisitEndDate = clientColumnMaps.get(MaternityDbConstants.Column.OpdDetails.CURRENT_VISIT_END_DATE);

            if (strVisitEndDate != null && MaternityLibrary.getInstance().isPatientInTreatedState(strVisitEndDate)) {
                return;
            }

            if (!isDiagnoseScheduled) {
                opdRegisterActivity.startFormActivity(MaternityConstants.Form.OPD_CHECK_IN, commonPersonObjectClient.getCaseId(), null, injectedValues, entityTable);
            } else {
                opdRegisterActivity.startFormActivity(MaternityConstants.Form.OPD_DIAGNOSIS_AND_TREAT, commonPersonObjectClient.getCaseId(), null, injectedValues, entityTable);
            }
        }
    }

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        // Do nothing
        Timber.i("Client was clicked on OPD Register: %s", new Gson().toJson(commonPersonObjectClient));
    }
}

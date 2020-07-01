package org.smartregister.maternity.sample.activity;


import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.maternity.activity.BaseMaternityFormActivity;
import org.smartregister.maternity.sample.R;
import org.smartregister.maternity.sample.fragment.MaternityFormFragment;

public class MaternityFormActivity extends BaseMaternityFormActivity {

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        MaternityFormFragment maternityFormFragment = MaternityFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(R.id.container, maternityFormFragment).commit();
    }
}

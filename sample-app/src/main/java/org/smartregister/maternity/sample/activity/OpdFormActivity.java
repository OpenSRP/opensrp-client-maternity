package org.smartregister.maternity.sample.activity;


import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.maternity.activity.BaseOpdFormActivity;
import org.smartregister.maternity.sample.fragment.OpdFormFragment;

public class OpdFormActivity extends BaseOpdFormActivity {

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        OpdFormFragment opdFormFragment = OpdFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, opdFormFragment).commit();
    }
}

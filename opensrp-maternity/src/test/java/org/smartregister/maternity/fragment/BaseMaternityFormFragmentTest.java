package org.smartregister.maternity.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentHostCallback;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.BuildConfig;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityFormActivity;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.configuration.BaseMaternityRegisterProviderMetadata;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.configuration.MaternityRegisterQueryProviderContract;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.repository.Repository;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(MockitoJUnitRunner.class)
public class BaseMaternityFormFragmentTest {

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }

    @Test
    public void startActivityOnLookUpShouldCallStartActivity() {
        MaternityConfiguration maternityConfiguration = new MaternityConfiguration.Builder(MaternityRegisterQueryProvider.class)
                .setMaternityRegisterProviderMetadata(BaseMaternityRegisterProviderMetadata.class)
                .setMaternityMetadata(new MaternityMetadata("form-name"
                        , "table-name"
                        , "register-event-type"
                        , "update-event-type"
                        , "config"
                        , BaseMaternityFormActivity.class
                        , BaseMaternityProfileActivity.class
                        , false))
                .build();

        MaternityLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(Repository.class), maternityConfiguration, BuildConfig.VERSION_CODE, 1);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        BaseMaternityFormFragment baseMaternityFormFragment = new BaseMaternityFormFragment();

        FragmentHostCallback host = Mockito.mock(FragmentHostCallback.class);

        ReflectionHelpers.setField(baseMaternityFormFragment, "mHost", host);
        baseMaternityFormFragment.startActivityOnLookUp(client);

        Mockito.verify(host, Mockito.times(1))
                .onStartActivityFromFragment(Mockito.any(Fragment.class)
                        , Mockito.any(Intent.class)
                        , Mockito.eq(-1)
                        , Mockito.nullable(Bundle.class));
    }

    @Test
    public void onItemClickShouldCallStartActivityOnLookupWithTheCorrectClient() {
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        BaseMaternityFormFragment baseMaternityFormFragment = Mockito.spy(new BaseMaternityFormFragment());
        Mockito.doNothing().when(baseMaternityFormFragment).startActivityOnLookUp(Mockito.any(CommonPersonObjectClient.class));

        AlertDialog alertDialog = Mockito.mock(AlertDialog.class);
        Mockito.doReturn(true).when(alertDialog).isShowing();
        Mockito.doNothing().when(alertDialog).dismiss();
        ReflectionHelpers.setField(baseMaternityFormFragment, "alertDialog", alertDialog);

        View clickedView = Mockito.mock(View.class);
        Mockito.doReturn(client).when(clickedView).getTag();

        // The actual method call
        baseMaternityFormFragment.onItemClick(clickedView);

        // Verification
        Mockito.verify(baseMaternityFormFragment, Mockito.times(1))
                .startActivityOnLookUp(Mockito.eq(client));
    }

    static class MaternityRegisterQueryProvider extends MaternityRegisterQueryProviderContract {

        @NonNull
        @Override
        public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
            return null;
        }

        @NonNull
        @Override
        public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
            return new String[0];
        }

        @NonNull
        @Override
        public String mainSelectWhereIDsIn() {
            return null;
        }
    }
}
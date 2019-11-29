package org.smartregister.opd.provider;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.BuildConfig;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.configuration.BaseOpdRegisterProviderMetadata;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;
import org.smartregister.opd.configuration.OpdRegisterRowOptions;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.repository.Repository;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-24
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class OpdRegisterProviderTest extends BaseTest {

    private OpdRegisterProvider opdRegisterProvider;

    @Mock
    private Context context;

    @Mock
    private View.OnClickListener onClickListener;

    @Mock
    private View.OnClickListener paginationClickListener;

    private BaseOpdRegisterProviderMetadata opdRegisterProviderMetadata;

    @Mock
    private View mockedView;

    @Mock
    private LayoutInflater inflator;

    @Before
    public void setUp() throws Exception {
        opdRegisterProviderMetadata = Mockito.spy(new BaseOpdRegisterProviderMetadata());
        Mockito.doReturn(mockedView).when(inflator).inflate(Mockito.anyInt(), Mockito.any(ViewGroup.class), Mockito.anyBoolean());
        Mockito.doReturn(inflator).when(context).getSystemService(Mockito.eq(Context.LAYOUT_INFLATER_SERVICE));

        OpdConfiguration opdConfiguration = new OpdConfiguration.Builder(OpdRegisterQueryProvider.class)
                .setOpdRegisterProviderMetadata(BaseOpdRegisterProviderMetadata.class)
                .build();

        OpdLibrary.init(Mockito.mock(org.smartregister.Context.class), Mockito.mock(Repository.class), opdConfiguration, BuildConfig.VERSION_CODE, 1);

        opdRegisterProvider = new OpdRegisterProvider(context, onClickListener, paginationClickListener);
        ReflectionHelpers.setField(opdRegisterProvider, "opdRegisterProviderMetadata", opdRegisterProviderMetadata);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void populatePatientColumnShouldCallProviderMetadataForDataValues() {
        CoreLibrary.init(PowerMockito.mock(org.smartregister.Context.class), PowerMockito.mock(SyncConfiguration.class));
        PowerMockito.mockStatic(Utils.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        Mockito.doReturn(true)
                .when(opdRegisterProviderMetadata)
                .isClientHaveGuardianDetails(Mockito.any(Map.class));
        Mockito.doReturn("2016-07-24T03:00:00.000+03:00")
                .when(opdRegisterProviderMetadata)
                .getDob(Mockito.any(Map.class));
        PowerMockito.when(Utils.getDuration("2016-07-24T03:00:00.000+03:00")).thenReturn("3y 4m");

        Resources resources = Mockito.mock(Resources.class);
        Mockito.doReturn(resources).when(context).getResources();
        Mockito.doReturn("CG").when(resources).getString(R.string.care_giver_initials);
        Mockito.doReturn("y").when(resources).getString(R.string.abbrv_years);

        OpdRegisterViewHolder viewHolder = Mockito.mock(OpdRegisterViewHolder.class);
        viewHolder.childColumn = Mockito.mock(View.class);
        viewHolder.dueButton = Mockito.mock(Button.class);

        opdRegisterProvider.populatePatientColumn(client, viewHolder);

        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getGuardianFirstName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getGuardianLastName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getGuardianMiddleName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getClientFirstName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getClientMiddleName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getClientLastName(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getDob(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getRegisterType(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getHomeAddress(Mockito.eq(client.getColumnmaps()));
        Mockito.verify(opdRegisterProviderMetadata, Mockito.times(1))
                .getGender(Mockito.eq(client.getColumnmaps()));
    }

    @Test
    public void createViewHolderShouldUseCustomViewHolderinRowOptions() {
        OpdRegisterRowOptions rowOptions = Mockito.mock(OpdRegisterRowOptions.class);
        ReflectionHelpers.setField(opdRegisterProvider, "opdRegisterRowOptions", rowOptions);
        Mockito.doReturn(true).when(rowOptions).isCustomViewHolder();

        opdRegisterProvider.createViewHolder(Mockito.mock(ViewGroup.class));

        Mockito.verify(rowOptions, Mockito.times(1)).createCustomViewHolder(Mockito.any(View.class));
    }

    @Test
    public void createViewHolderShouldUseCustomLayoutIdProvided() {
        int layoutId = 49834;

        OpdRegisterRowOptions rowOptions = Mockito.mock(OpdRegisterRowOptions.class);
        ReflectionHelpers.setField(opdRegisterProvider, "opdRegisterRowOptions", rowOptions);
        Mockito.doReturn(true).when(rowOptions).useCustomViewLayout();
        Mockito.doReturn(layoutId).when(rowOptions).getCustomViewLayoutId();

        opdRegisterProvider.createViewHolder(Mockito.mock(ViewGroup.class));

        Mockito.verify(rowOptions, Mockito.times(2)).getCustomViewLayoutId();
        Mockito.verify(inflator, Mockito.times(1)).inflate(Mockito.eq(layoutId), Mockito.any(ViewGroup.class), Mockito.anyBoolean());
    }

    @Test
    public void getViewShouldCallRowOptionsPopulateClientRowWhenDefaultCustomImplementationIsProvided() {
        OpdRegisterRowOptions rowOptions = Mockito.mock(OpdRegisterRowOptions.class);
        ReflectionHelpers.setField(opdRegisterProvider, "opdRegisterRowOptions", rowOptions);

        Mockito.doReturn(true).when(rowOptions).isDefaultPopulatePatientColumn();

        opdRegisterProvider.getView(Mockito.mock(Cursor.class)
                , Mockito.mock(CommonPersonObjectClient.class)
                , Mockito.mock(OpdRegisterViewHolder.class));

        Mockito.verify(rowOptions, Mockito.times(1)).populateClientRow(
                Mockito.any(Cursor.class)
                , Mockito.any(CommonPersonObjectClient.class)
                , Mockito.any(SmartRegisterClient.class)
                , Mockito.any(OpdRegisterViewHolder.class));
    }

    static class OpdRegisterQueryProvider extends OpdRegisterQueryProviderContract {

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
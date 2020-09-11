package org.smartregister.maternity.tasks;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityFormActivity;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.configuration.BaseMaternityRegisterProviderMetadata;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.provider.MaternityRegisterQueryProviderTest;
import org.smartregister.maternity.repository.MaternityDetailsRepository;
import org.smartregister.maternity.repository.MaternityRegistrationDetailsRepository;
import org.smartregister.repository.EventClientRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class FetchRegistrationDataTaskTest {

    private FetchRegistrationDataTask fetchRegistrationDataTask;

    @Mock
    private FetchRegistrationDataTask.OnTaskComplete onTaskComplete;

    @Mock
    private Context context;

    @Mock
    private MaternityLibrary maternityLibrary;

    @Mock
    private EventClientRepository eventClientRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        fetchRegistrationDataTask = new FetchRegistrationDataTask(contextWeakReference, onTaskComplete);
    }

    @Test
    public void testDoInBackgroundReturnsCorrectValue() {
        org.smartregister.Context opensrpContext = Mockito.mock(org.smartregister.Context.class);
        SQLiteDatabase sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
        MaternityDetailsRepository maternityDetailsRepository = new MaternityDetailsRepository();
        Mockito.doReturn(sqLiteDatabase).when(eventClientRepository).getReadableDatabase();
        Mockito.doReturn(eventClientRepository).when(opensrpContext).getEventClientRepository();
        MaternityConfiguration maternityConfiguration = new MaternityConfiguration.Builder(MaternityRegisterQueryProviderTest.class)
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
        Mockito.doReturn(maternityConfiguration).when(maternityLibrary).getMaternityConfiguration();
        Mockito.doReturn(opensrpContext).when(maternityLibrary).context();
        Mockito.doReturn(new MaternityRegistrationDetailsRepository()).when(maternityLibrary).getMaternityRegistrationDetailsRepository();
        Mockito.doReturn(maternityDetailsRepository).when(maternityLibrary).getMaternityDetailsRepository();
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);

        HashMap<String, String> details = new HashMap<>();
        details.put("first_name", "John");
        details.put("last_name", "John");
        ArrayList<HashMap<String, String>> mapArrayList = new ArrayList<>();
        mapArrayList.add(details);

        Mockito.doReturn(mapArrayList).when(eventClientRepository).rawQuery(Mockito.eq(sqLiteDatabase), Mockito.anyString());

        String baseEntityId = "2323-ae";
        fetchRegistrationDataTask.doInBackground(baseEntityId);
        Mockito.verify(eventClientRepository, Mockito.times(1)).rawQuery(Mockito.eq(sqLiteDatabase), Mockito.anyString());

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }
}
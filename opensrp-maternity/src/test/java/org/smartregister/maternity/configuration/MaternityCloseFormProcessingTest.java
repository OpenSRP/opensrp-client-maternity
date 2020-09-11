package org.smartregister.maternity.configuration;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class MaternityCloseFormProcessingTest {

    private MaternityCloseFormProcessing maternityCloseFormProcessing;

    @Before
    public void setUp() {

        CoreLibrary coreLibrary = mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        MaternityLibrary maternityLibrary = mock(MaternityLibrary.class);
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);

        maternityCloseFormProcessing = new MaternityCloseFormProcessing();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
        maternityCloseFormProcessing = null;
    }

    @Test
    public void processMaternityFormShouldReturnValidEventList() throws JSONException {
        String jsonString = "{\"encounter_type\":\"Maternity Close\",\"entity_id\":\"\",\"metadata\":{\"encounter_location\":\"\"},\"step1\":{\"title\":\"Maternity Close\",\"fields\":[{\"key\":\"maternity_close_reason\",\"value\":\"woman_died\"},{\"key\":\"date_of_death\",\"value\":\"09-08-2020\"},{\"key\":\"place_of death\",\"value\":\"Community\"},{\"key\":\"death_cause\",\"value\":\"Unknown\"}]}}";

        JSONObject clientObject = new JSONObject();
        clientObject.put(MaternityConstants.JSON_FORM_KEY.ATTRIBUTES, clientObject);

        Context context = mock(Context.class);
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
        Intent intent = mock(Intent.class);
        EventClientRepository eventClientRepository = mock(EventClientRepository.class);
        MaternityConfiguration maternityConfiguration = mock(MaternityConfiguration.class);
        MaternityMetadata maternityMetadata = mock(MaternityMetadata.class);

        doReturn(context).when(CoreLibrary.getInstance()).context();
        doReturn(allSharedPreferences).when(context).allSharedPreferences();
        doReturn("").when(allSharedPreferences).fetchRegisteredANM();
        doReturn(0).when(MaternityLibrary.getInstance()).getApplicationVersion();
        doReturn(0).when(MaternityLibrary.getInstance()).getDatabaseVersion();
        doReturn(true).when(intent).hasExtra(eq(MaternityConstants.IntentKey.BASE_ENTITY_ID));
        doReturn("3242-23-423-4-234-234").when(intent).getStringExtra(eq(MaternityConstants.IntentKey.BASE_ENTITY_ID));
        doReturn(true).when(intent).hasExtra(eq(MaternityConstants.IntentKey.ENTITY_TABLE));
        doReturn("ec_client").when(intent).getStringExtra(eq(MaternityConstants.IntentKey.ENTITY_TABLE));
        doReturn(eventClientRepository).when(MaternityLibrary.getInstance()).eventClientRepository();
        doReturn(clientObject).when(eventClientRepository).getClientByBaseEntityId(anyString());
        doReturn(maternityConfiguration).when(MaternityLibrary.getInstance()).getMaternityConfiguration();
        doReturn(maternityMetadata).when(maternityConfiguration).getMaternityMetadata();
        doReturn(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION).when(maternityMetadata).getUpdateEventType();

        List<Event> events = maternityCloseFormProcessing.processMaternityForm(jsonString, intent);

        assertEquals(1, events.size());
        assertEquals(MaternityConstants.EventType.DEATH, events.get(0).getEventType());
        assertEquals("3242-23-423-4-234-234", events.get(0).getBaseEntityId());
        assertEquals("ec_client", events.get(0).getEntityType());
    }
}

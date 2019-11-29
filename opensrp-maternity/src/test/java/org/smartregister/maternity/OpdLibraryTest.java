package org.smartregister.maternity;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.maternity.configuration.OpdConfiguration;
import org.smartregister.maternity.shadows.ShadowOpdLibrary;
import org.smartregister.maternity.utils.OpdConstants;
import org.smartregister.maternity.utils.OpdDbConstants;
import org.smartregister.maternity.utils.OpdJsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-24
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowOpdLibrary.class})
public class OpdLibraryTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void initShouldCreateNewLibraryInstanceWhenInstanceIsNull() {
        assertNull(ReflectionHelpers.getStaticField(OpdLibrary.class, "instance"));

        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(ReflectionHelpers.getStaticField(OpdLibrary.class, "instance"));
    }

    @Test
    public void getInstanceShouldThrowIllegalStateException() throws Throwable {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Instance does not exist!!! Call org.smartregister.opd.OpdLibrary"
                + ".init method in the onCreate method of "
                + "your Application class");

        OpdLibrary.getInstance();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void getOpdRulesEngineHelperShouldReturnNonNull() {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(OpdLibrary.getInstance().getOpdRulesEngineHelper());
    }

    @Test
    public void getLatestValidCheckInDateShouldReturn1DayFromNow() {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        long timeNow = new Date().getTime();

        assertEquals(timeNow - 24 * 60 * 60 * 1000, OpdLibrary.getInstance().getLatestValidCheckInDate().getTime(), 100);
    }

    @Test
    public void processOpdCheckInFormShouldValidOpdCheckInEventFromJsonForm() throws JSONException {
        int applicationVersion = 34;
        int databaseVersion = 3;
        String providerId = "elisadoe";
        String defaultTeam = "Lizulu Team";
        String defaultTeamId = "90239-dsdkl-329d";
        String baseEntityId = "8923-dwef-28ds";
        String locationId = "mylocation-id";

        Context context = Mockito.mock(Context.class);
        OpdLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), applicationVersion, databaseVersion);

        // Mock call to CoreLibrary.getInstance().context().allSharedPreferences()
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Mockito.doReturn(context).when(coreLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn(providerId).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(defaultTeam).when(allSharedPreferences).fetchDefaultTeam(Mockito.eq(providerId));
        Mockito.doReturn(defaultTeamId).when(allSharedPreferences).fetchDefaultTeamId(Mockito.eq(providerId));
        Mockito.doReturn(locationId).when(allSharedPreferences).fetchUserLocalityId(Mockito.eq(providerId));

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        // Create the OPD-CHECKIN Form here
        JSONObject opdForm = new JSONObject();
        JSONObject stepOne = new JSONObject();
        JSONArray fields = new JSONArray();

        stepOne.put(OpdJsonFormUtils.FIELDS, fields);
        opdForm.put(OpdJsonFormUtils.STEP1, stepOne);
        opdForm.put(OpdJsonFormUtils.METADATA, new JSONObject());

        Intent intentData = new Intent();
        intentData.putExtra(OpdConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);
        intentData.putExtra(OpdConstants.IntentKey.ENTITY_TABLE, "ec_clients");

        String jsonString = opdForm.toString();
        Event opdCheckInEvent = OpdLibrary.getInstance().processOpdCheckInForm(OpdConstants.EventType.CHECK_IN, jsonString, intentData);

        assertNotNull(opdCheckInEvent.getDetails().get(OpdConstants.Event.CheckIn.Detail.VISIT_DATE));
        assertNotNull(opdCheckInEvent.getDetails().get(OpdConstants.Event.CheckIn.Detail.VISIT_ID));
        assertNull(opdCheckInEvent.getEventId());
        assertEquals(applicationVersion, (int) opdCheckInEvent.getClientApplicationVersion());
        assertEquals(databaseVersion, (int) opdCheckInEvent.getClientDatabaseVersion());
        assertEquals(providerId, opdCheckInEvent.getProviderId());
    }


//TODO: Fix Robolectric not mocking calendar & date class using shadow class and then these two tests are going to be feasible

    @Test
    public void isPatientInTreatedStateShouldReturnTrueWhenCurrentDateIsBeforeMidnightOfTreatmentDate() throws ParseException {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-04 20:23:20");

        ShadowOpdLibrary.setMockedTime(date.getTime());
        Assert.assertTrue(OpdLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }

    @Test
    public void isPatientInTreatedStateShouldReturnFalseWhenCurrentDateIsAfterMidnightOfTreatmentDate() throws ParseException {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-05 20:23:20");

        ShadowOpdLibrary.setMockedTime(date.getTime());
        Assert.assertFalse(OpdLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }
}
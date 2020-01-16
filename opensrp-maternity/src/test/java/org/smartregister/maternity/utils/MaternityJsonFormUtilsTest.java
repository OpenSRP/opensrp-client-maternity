package org.smartregister.maternity.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.maternity.BuildConfig;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.configuration.MaternityRegisterQueryProviderTest;
import org.smartregister.maternity.pojos.MaternityMetadata;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;

@PrepareForTest(MaternityUtils.class)
@RunWith(PowerMockRunner.class)
public class MaternityJsonFormUtilsTest {

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }

    @Test
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndEntityIdBlank() throws Exception {
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.JSON_FORM_KEY.NAME
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , MaternityConstants.CONFIG
                , Class.class
                , Class.class
                , true);
        MaternityConfiguration maternityConfiguration = new MaternityConfiguration.Builder(MaternityRegisterQueryProviderTest.class)
                .setMaternityMetadata(maternityMetadata)
                .build();

        MaternityLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), maternityConfiguration,
                BuildConfig.VERSION_CODE, 1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("metadata", new JSONObject());
        JSONObject result = MaternityJsonFormUtils.getFormAsJson(jsonObject, MaternityConstants.JSON_FORM_KEY.NAME, "", "");
        Assert.assertNull(result);
    }

    @Test
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndEntityIdNonEmpty() throws Exception {
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.JSON_FORM_KEY.NAME
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , MaternityConstants.CONFIG
                , Class.class
                , Class.class
                , true);

        MaternityConfiguration maternityConfiguration = new MaternityConfiguration.Builder(MaternityRegisterQueryProviderTest.class)
                .setMaternityMetadata(maternityMetadata)
                .build();

        MaternityLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), maternityConfiguration,
                BuildConfig.VERSION_CODE, 1);

        JSONObject jsonArrayFieldsJsonObject = new JSONObject();
        jsonArrayFieldsJsonObject.put(MaternityJsonFormUtils.KEY, MaternityJsonFormUtils.OPENSRP_ID);

        JSONArray jsonArrayFields = new JSONArray();
        jsonArrayFields.put(jsonArrayFieldsJsonObject);

        JSONObject jsonObjectForFields = new JSONObject();
        jsonObjectForFields.put(MaternityJsonFormUtils.FIELDS, jsonArrayFields);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("metadata", new JSONObject());
        jsonObject.put(MaternityJsonFormUtils.STEP1, jsonObjectForFields);

        JSONObject result = MaternityJsonFormUtils.getFormAsJson(jsonObject, MaternityConstants.JSON_FORM_KEY.NAME, "23", "currentLocation");
        Assert.assertEquals(result, jsonObject);
    }

    @Test
    public void testGetFormAsJsonWithNonEmptyJsonObjectAndInjectableFields() throws Exception {
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.JSON_FORM_KEY.NAME
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , MaternityConstants.CONFIG
                , Class.class
                , Class.class
                , true);

        MaternityConfiguration maternityConfiguration = new MaternityConfiguration.Builder(MaternityRegisterQueryProviderTest.class)
                .setMaternityMetadata(maternityMetadata)
                .build();

        MaternityLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), maternityConfiguration,
                BuildConfig.VERSION_CODE, 1);

        JSONObject jsonArrayFieldsJsonObject = new JSONObject();
        jsonArrayFieldsJsonObject.put(MaternityJsonFormUtils.KEY, MaternityJsonFormUtils.OPENSRP_ID);

        JSONObject injectableField = new JSONObject();
        injectableField.put(MaternityJsonFormUtils.KEY, "Injectable");

        JSONArray jsonArrayFields = new JSONArray();
        jsonArrayFields.put(jsonArrayFieldsJsonObject);
        jsonArrayFields.put(injectableField);

        JSONObject jsonObjectForFields = new JSONObject();
        jsonObjectForFields.put(MaternityJsonFormUtils.FIELDS, jsonArrayFields);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("metadata", new JSONObject());
        jsonObject.put(MaternityJsonFormUtils.STEP1, jsonObjectForFields);

        HashMap<String, String> injectableFields = new HashMap<>();
        injectableFields.put("Injectable", "Injectable value");
        JSONObject result = MaternityJsonFormUtils.getFormAsJson(jsonObject, MaternityConstants.JSON_FORM_KEY.NAME, "23", "currentLocation", injectableFields);
        Assert.assertEquals(result, jsonObject);
        Assert.assertEquals("Injectable value", injectableField.getString(MaternityJsonFormUtils.VALUE));
    }

    @Test
    public void testAddLocationTreeWithEmptyJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        Whitebox.invokeMethod(MaternityJsonFormUtils.class, "addLocationTree", "", jsonObject, "");
        Assert.assertFalse(jsonObject.has("tree"));
    }

    @Test
    public void testAddLocationTreeWithNonEmptyJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityJsonFormUtils.KEY, "");
        JSONArray jsonArray = new JSONArray();
        Whitebox.invokeMethod(MaternityJsonFormUtils.class, "addLocationTree", "", jsonObject, jsonArray.toString());
        Assert.assertTrue(jsonObject.has("tree"));
    }

    @Test
    public void testAddLocationDefaultWithEmptyJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        Whitebox.invokeMethod(MaternityJsonFormUtils.class, "addLocationDefault", "", jsonObject, "");
        Assert.assertFalse(jsonObject.has("default"));
    }

    @Test
    public void testAddLocationDefaultTreeWithNonEmptyJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityJsonFormUtils.KEY, "");
        JSONArray jsonArray = new JSONArray();
        Whitebox.invokeMethod(MaternityJsonFormUtils.class, "addLocationDefault", "", jsonObject, jsonArray.toString());
        Assert.assertTrue(jsonObject.has("default"));
    }

    @Test
    public void testTagSyncMetadataWithEmptyEvent() throws Exception {
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.JSON_FORM_KEY.NAME
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , MaternityConstants.CONFIG
                , Class.class
                , Class.class
                , true);
        MaternityConfiguration maternityConfiguration = new MaternityConfiguration
                .Builder(null)
                .setMaternityMetadata(maternityMetadata)
                .build();
        MaternityLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), maternityConfiguration,
                BuildConfig.VERSION_CODE, 1);
        CoreLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(SyncConfiguration.class));

        PowerMockito.when(MaternityUtils.class, "getAllSharedPreferences").thenReturn(PowerMockito.mock(AllSharedPreferences.class));

        Event event = MaternityJsonFormUtils.tagSyncMetadata(new Event());
        Assert.assertNotNull(event);
    }

    @Test
    public void testGetLocationIdWithCurrentLocalityIsNotNull() throws Exception {
        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.JSON_FORM_KEY.NAME
                , MaternityDbConstants.KEY.TABLE
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , MaternityConstants.CONFIG
                , Class.class
                , Class.class
                , true);
        maternityMetadata.setHealthFacilityLevels(new ArrayList<String>());
        MaternityConfiguration maternityConfiguration = new MaternityConfiguration
                .Builder(MaternityRegisterQueryProviderTest.class)
                .setMaternityMetadata(maternityMetadata)
                .build();
        MaternityLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), maternityConfiguration,
                BuildConfig.VERSION_CODE, 1);
        CoreLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(SyncConfiguration.class));

        ArrayList<String> defaultLocations = new ArrayList<>();
        defaultLocations.add("Country");
        LocationHelper.init(defaultLocations,
                "Country");
        AllSharedPreferences allSharedPreferences = PowerMockito.mock(AllSharedPreferences.class);
        PowerMockito.when(allSharedPreferences, "fetchCurrentLocality").thenReturn("Place");
        Assert.assertNotNull(LocationHelper.getInstance());
        String result = MaternityJsonFormUtils.getLocationId("Country", allSharedPreferences);
        Assert.assertEquals("Place", result);
    }

    @Test
    public void testValidateParameters() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormUtils.KEY, new JSONArray());
        Triple<Boolean, JSONObject, JSONArray> result = MaternityJsonFormUtils.validateParameters(jsonObject.toString());
        Assert.assertNotNull(result);
    }

    @Test
    public void testProcessGenderReplaceMwithMale() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityConstants.KEY.KEY, MaternityConstants.SEX);
        jsonObject.put(MaternityConstants.KEY.VALUE, "m");
        jsonArray.put(jsonObject);
        MaternityJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals("Male", jsonArray.getJSONObject(0).get("value"));
    }

    @Test
    public void testProcessGenderReplaceFwithFemale() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityConstants.KEY.KEY, MaternityConstants.SEX);
        jsonObject.put(MaternityConstants.KEY.VALUE, "f");
        jsonArray.put(jsonObject);
        MaternityJsonFormUtils.processGender(jsonArray);

        Assert.assertEquals("Female", jsonArray.getJSONObject(0).get("value"));
    }

    @Test
    public void testProcessGenderShouldReplaceNothing() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityConstants.KEY.KEY, MaternityConstants.SEX);
        jsonObject.put(MaternityConstants.KEY.VALUE, "L");
        jsonArray.put(jsonObject);
        MaternityJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals("", jsonArray.getJSONObject(0).get("value"));
    }

    @Test
    public void testProcessGenderCheckNullOnGenderJsonObject() {
        JSONArray jsonArray = new JSONArray();
        MaternityJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals(jsonArray.length(), 0);
    }

    @Test
    public void testProcessGenderShouldThrowJSONException() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityConstants.KEY.KEY, MaternityConstants.SEX);
        jsonArray.put(jsonObject);
        MaternityJsonFormUtils.processGender(jsonArray);
        Assert.assertEquals(jsonArray.getJSONObject(0).length(), 1);
    }

    @Test
    public void testProcessLocationFields() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TREE);
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put("test");
        jsonObject.put(JsonFormConstants.VALUE, jsonArray1.toString());
        jsonArray.put(jsonObject);
        ArrayList<String> defaultLocations = new ArrayList<>();
        defaultLocations.add("Country");
        CoreLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(SyncConfiguration.class));
        LocationHelper.init(defaultLocations,
                "Country");
        MaternityJsonFormUtils.processLocationFields(jsonArray);
        Assert.assertEquals(jsonArray.getJSONObject(0).getString(JsonFormConstants.VALUE), "test");
    }

    @Test
    public void testLastInteractedWithEmpty() {
        JSONArray jsonArray = new JSONArray();
        MaternityJsonFormUtils.lastInteractedWith(jsonArray);
        Assert.assertEquals(jsonArray.length(), 1);
    }

    @Test
    public void testDobUnknownUpdateFromAge() throws JSONException {
        JSONArray jsonArrayFields = new JSONArray();

        JSONArray jsonArrayDobUnknown = new JSONArray();
        JSONObject jsonObjectOptions = new JSONObject();
        jsonObjectOptions.put(MaternityConstants.KEY.VALUE, Boolean.TRUE.toString());
        jsonArrayDobUnknown.put(jsonObjectOptions);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormUtils.KEY, MaternityConstants.JSON_FORM_KEY.DOB_UNKNOWN);
        jsonObject.put(MaternityConstants.JSON_FORM_KEY.OPTIONS, jsonArrayDobUnknown);

        JSONObject jsonObjectDob = new JSONObject();
        jsonObjectDob.put(JsonFormUtils.KEY, MaternityConstants.JSON_FORM_KEY.DOB_ENTERED);

        JSONObject jsonObjectAgeEntered = new JSONObject();
        jsonObjectAgeEntered.put(JsonFormUtils.KEY, MaternityConstants.JSON_FORM_KEY.AGE_ENTERED);
        jsonObjectAgeEntered.put(JsonFormUtils.VALUE, "34");


        jsonArrayFields.put(jsonObject);
        jsonArrayFields.put(jsonObjectAgeEntered);
        jsonArrayFields.put(jsonObjectDob);

        String expected = "[{\"options\":[{\"value\":\"true\"}],\"key\":\"dob_unknown\"},{\"value\":\"34\",\"key\":\"age_entered\"}," +
                "{\"value\":\"01-01-1985\",\"key\":\"dob_entered\"},{\"openmrs_entity\":\"person\"," +
                "\"openmrs_entity_id\":\"birthdate_estimated\",\"value\":1,\"key\":\"birthdate_estimated\"}]";

        MaternityJsonFormUtils.dobUnknownUpdateFromAge(jsonArrayFields);

        Assert.assertEquals(expected, jsonArrayFields.toString());
    }

    @Test
    public void testProcessReminderSetToTrue() throws Exception {
        JSONArray jsonArrayFields = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityConstants.KEY.KEY, MaternityConstants.JSON_FORM_KEY.REMINDERS);

        JSONArray jsonArrayOptions = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(MaternityConstants.KEY.VALUE, Boolean.toString(true));
        jsonArrayOptions.put(jsonObject1);

        jsonObject.put(MaternityConstants.JSON_FORM_KEY.OPTIONS, jsonArrayOptions);
        jsonArrayFields.put(jsonObject);

        Whitebox.invokeMethod(MaternityJsonFormUtils.class, "processReminder", jsonArrayFields);

        String expected = "[{\"options\":[{\"value\":\"true\"}],\"value\":1,\"key\":\"reminders\"}]";
        Assert.assertEquals(expected, jsonArrayFields.toString());

    }

    @Test
    public void testProcessReminderSetToFalse() throws Exception {
        JSONArray jsonArrayFields = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityConstants.KEY.KEY, MaternityConstants.JSON_FORM_KEY.REMINDERS);

        JSONArray jsonArrayOptions = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(MaternityConstants.KEY.VALUE, Boolean.toString(false));
        jsonArrayOptions.put(jsonObject1);

        jsonObject.put(MaternityConstants.JSON_FORM_KEY.OPTIONS, jsonArrayOptions);
        jsonArrayFields.put(jsonObject);

        Whitebox.invokeMethod(MaternityJsonFormUtils.class, "processReminder", jsonArrayFields);

        String expected = "[{\"options\":[{\"value\":\"false\"}],\"value\":0,\"key\":\"reminders\"}]";
        Assert.assertEquals(expected, jsonArrayFields.toString());

    }

    @Test
    public void testFieldsHasEmptyStep() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = MaternityJsonFormUtils.fields(jsonObject, "");
        Assert.assertNull(jsonArray);
    }

    @Test
    public void testFieldHasStep() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String step = "STEP1";
        JSONObject jsonObjectWithFields = new JSONObject();
        jsonObjectWithFields.put(MaternityJsonFormUtils.FIELDS, new JSONArray());
        jsonObject.put(step, jsonObjectWithFields);
        JSONArray jsonArray = MaternityJsonFormUtils.fields(jsonObject, step);
        Assert.assertNotNull(jsonArray);
    }

    @Test
    public void testFormTagShouldReturnValidFormTagObject() {
        MaternityLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class),
                BuildConfig.VERSION_CODE, 1);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("1");
        FormTag formTag = MaternityJsonFormUtils.formTag(allSharedPreferences);
        Assert.assertTrue((BuildConfig.VERSION_CODE == formTag.appVersion));
        Assert.assertTrue((formTag.databaseVersion == 1));
        Assert.assertEquals("1", formTag.providerId);
    }

    @Test
    public void testGetFieldValueShouldReturnNullWithInvalidJsonString() {
        Assert.assertNull(MaternityJsonFormUtils.getFieldValue("", "", ""));
    }

    @Test
    public void testGetFieldValueShouldReturnNullWithValidJsonStringWithoutStepKey() throws JSONException {
        JSONObject jsonForm = new JSONObject();
        JSONObject jsonStep = new JSONObject();
        jsonStep.put(MaternityJsonFormUtils.FIELDS, new JSONArray());
        Assert.assertNull(MaternityJsonFormUtils.getFieldValue(jsonForm.toString(), MaternityJsonFormUtils.STEP1, ""));

    }

    @Test
    public void testGetFieldValueShouldReturnPassedValue() throws JSONException {
        JSONObject jsonForm = new JSONObject();
        JSONObject jsonStep = new JSONObject();
        JSONArray jsonArrayFields = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MaternityJsonFormUtils.KEY, MaternityConstants.JSON_FORM_KEY.REMINDERS);
        jsonObject.put(MaternityJsonFormUtils.VALUE, "some reminder");
        jsonArrayFields.put(jsonObject);
        jsonStep.put(MaternityJsonFormUtils.FIELDS, jsonArrayFields);
        jsonForm.put(MaternityJsonFormUtils.STEP1, jsonStep);

        Assert.assertEquals("some reminder", MaternityJsonFormUtils.getFieldValue(jsonForm.toString(), MaternityJsonFormUtils.STEP1, MaternityConstants.JSON_FORM_KEY.REMINDERS));
    }

    @Test
    public void testProcessOpdDetailsFormShouldReturnNullJsonFormNull() {
        Assert.assertNull(MaternityJsonFormUtils.processMaternityDetailsForm("", Mockito.mock(FormTag.class)));
    }

}

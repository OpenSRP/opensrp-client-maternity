package org.smartregister.opd.utils;

import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojos.OpdMetadata;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

@RunWith(RobolectricTestRunner.class)
public class OpdUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private OpdConfiguration opdConfiguration;

    @Mock
    private OpdMetadata opdMetadata;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", OpdUtils.fillTemplate(template, facts));
    }

    @Test
    public void convertStringToDate() {
        Date date = OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, "2019-10-28 18:09:49");
        assertEquals("2019-10-28 18:09:49", OpdUtils.convertDate(date, OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void generateNIds() {
        String result = OpdUtils.generateNIds(0);
        assertEquals(result, "");

        String result1 = OpdUtils.generateNIds(1);
        assertEquals(result1.split(",").length, 1);
    }

    @Test
    public void getIntentValue() {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.hasExtra("test")).thenReturn(false);
        assertNull(OpdUtils.getIntentValue(intent, "test"));

        Mockito.when(intent.hasExtra("test")).thenReturn(true);
        Mockito.when(intent.getStringExtra("test")).thenReturn("test");
        assertEquals("test", OpdUtils.getIntentValue(intent, "test"));
    }

    @Test
    public void getIntentValueReturnNull() {
        assertNull(OpdUtils.getIntentValue(null, "test"));
    }

    @Test
    public void metadata() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.doReturn(opdConfiguration).when(opdLibrary).getOpdConfiguration();
        Mockito.doReturn(opdMetadata).when(opdConfiguration).getOpdMetadata();

        assertEquals(opdMetadata, OpdUtils.metadata());

        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void testGetClientAge(){
        assertEquals("13", OpdUtils.getClientAge("13y 4m", "y"));
        assertEquals("4m", OpdUtils.getClientAge("4m", "y"));
        assertEquals("5", OpdUtils.getClientAge("5y 4w", "y"));
        assertEquals("3y", OpdUtils.getClientAge("3y", "y"));
        assertEquals("5w 6d", OpdUtils.getClientAge("5w 6d", "y"));
        assertEquals("6d", OpdUtils.getClientAge("6d", "y"));
    }

    @Test
    public void isTemplateShouldReturnFalseIfStringDoesNotContainMatchingBraces() {
        assertFalse(OpdUtils.isTemplate("{ This is a sytling brace"));
        assertFalse(OpdUtils.isTemplate("This is display text"));
    }

    @Test
    public void isTemplateShouldReturnTrueIfStringContainsMatchingBraces() {
        assertTrue(OpdUtils.isTemplate("Project Name: {project_name}"));
    }

    @Test
    public void buildActivityFormIntentShouldCreateIntentWithWizardEnabledWhenEncounterTypeIsDiagnosisAndTreat() throws JSONException {
        // Mock calls to OpdLibrary
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.doReturn(opdConfiguration).when(opdLibrary).getOpdConfiguration();
        Mockito.doReturn(opdMetadata).when(opdConfiguration).getOpdMetadata();
        Mockito.doReturn(BaseOpdFormActivity.class).when(opdMetadata).getOpdFormActivity();

        JSONObject jsonForm = new JSONObject();
        jsonForm.put(OpdJsonFormUtils.ENCOUNTER_TYPE, OpdConstants.EventType.DIAGNOSIS_AND_TREAT);

        HashMap<String, String> parcelableData = new HashMap<>();
        String baseEntityId = "89283-23dsd-23sdf";
        parcelableData.put(OpdConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);

        Intent actualResult = OpdUtils.buildFormActivityIntent(jsonForm, parcelableData, Mockito.mock(Context.class));
        Form form = (Form) actualResult.getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM);

        assertTrue(form.isWizard());
        assertEquals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT, form.getName());
        assertEquals(baseEntityId, actualResult.getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID));
    }

}
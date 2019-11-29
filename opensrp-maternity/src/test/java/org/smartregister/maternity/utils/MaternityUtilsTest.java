package org.smartregister.maternity.utils;

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
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityFormActivity;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.pojos.MaternityMetadata;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(RobolectricTestRunner.class)
public class MaternityUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MaternityLibrary maternityLibrary;

    @Mock
    private MaternityConfiguration maternityConfiguration;

    @Mock
    private MaternityMetadata maternityMetadata;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", MaternityUtils.fillTemplate(template, facts));
    }

    @Test
    public void convertStringToDate() {
        Date date = MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, "2019-10-28 18:09:49");
        assertEquals("2019-10-28 18:09:49", MaternityUtils.convertDate(date, MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void generateNIds() {
        String result = MaternityUtils.generateNIds(0);
        assertEquals(result, "");

        String result1 = MaternityUtils.generateNIds(1);
        assertEquals(result1.split(",").length, 1);
    }

    @Test
    public void getIntentValue() {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.hasExtra("test")).thenReturn(false);
        assertNull(MaternityUtils.getIntentValue(intent, "test"));

        Mockito.when(intent.hasExtra("test")).thenReturn(true);
        Mockito.when(intent.getStringExtra("test")).thenReturn("test");
        assertEquals("test", MaternityUtils.getIntentValue(intent, "test"));
    }

    @Test
    public void getIntentValueReturnNull() {
        assertNull(MaternityUtils.getIntentValue(null, "test"));
    }

    @Test
    public void metadata() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        Mockito.doReturn(maternityConfiguration).when(maternityLibrary).getMaternityConfiguration();
        Mockito.doReturn(maternityMetadata).when(maternityConfiguration).getOpdMetadata();

        assertEquals(maternityMetadata, MaternityUtils.metadata());

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }

    @Test
    public void testGetClientAge(){
        assertEquals("13", MaternityUtils.getClientAge("13y 4m", "y"));
        assertEquals("4m", MaternityUtils.getClientAge("4m", "y"));
        assertEquals("5", MaternityUtils.getClientAge("5y 4w", "y"));
        assertEquals("3y", MaternityUtils.getClientAge("3y", "y"));
        assertEquals("5w 6d", MaternityUtils.getClientAge("5w 6d", "y"));
        assertEquals("6d", MaternityUtils.getClientAge("6d", "y"));
    }

    @Test
    public void isTemplateShouldReturnFalseIfStringDoesNotContainMatchingBraces() {
        assertFalse(MaternityUtils.isTemplate("{ This is a sytling brace"));
        assertFalse(MaternityUtils.isTemplate("This is display text"));
    }

    @Test
    public void isTemplateShouldReturnTrueIfStringContainsMatchingBraces() {
        assertTrue(MaternityUtils.isTemplate("Project Name: {project_name}"));
    }

    @Test
    public void buildActivityFormIntentShouldCreateIntentWithWizardEnabledWhenEncounterTypeIsDiagnosisAndTreat() throws JSONException {
        // Mock calls to MaternityLibrary
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        Mockito.doReturn(maternityConfiguration).when(maternityLibrary).getMaternityConfiguration();
        Mockito.doReturn(maternityMetadata).when(maternityConfiguration).getOpdMetadata();
        Mockito.doReturn(BaseMaternityFormActivity.class).when(maternityMetadata).getOpdFormActivity();

        JSONObject jsonForm = new JSONObject();
        jsonForm.put(MaternityJsonFormUtils.ENCOUNTER_TYPE, MaternityConstants.EventType.DIAGNOSIS_AND_TREAT);

        HashMap<String, String> parcelableData = new HashMap<>();
        String baseEntityId = "89283-23dsd-23sdf";
        parcelableData.put(MaternityConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);

        Intent actualResult = MaternityUtils.buildFormActivityIntent(jsonForm, parcelableData, Mockito.mock(Context.class));
        Form form = (Form) actualResult.getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM);

        assertTrue(form.isWizard());
        assertEquals(MaternityConstants.EventType.DIAGNOSIS_AND_TREAT, form.getName());
        assertEquals(baseEntityId, actualResult.getStringExtra(MaternityConstants.IntentKey.BASE_ENTITY_ID));
    }

}
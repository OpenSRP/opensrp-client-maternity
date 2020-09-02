package org.smartregister.maternity.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
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
import org.smartregister.maternity.pojo.MaternityMetadata;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

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

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        when(MaternityLibrary.getInstance().context()).thenReturn(mock(org.smartregister.Context.class));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
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
    public void testGenerateNIdsShouldGenerateNIds() {
        assertEquals(2, MaternityUtils.generateNIds(2).length);
        assertEquals(0, MaternityUtils.generateNIds(0).length);
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
        Mockito.doReturn(maternityMetadata).when(maternityConfiguration).getMaternityMetadata();

        assertEquals(maternityMetadata, MaternityUtils.metadata());

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }

    @Test
    public void testGetClientAge() {
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
    public void buildActivityFormIntentShouldCreateIntentWithWizardEnabledWhenFormHasMoreThanOneStep() throws JSONException {
        // Mock calls to MaternityLibrary
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        Mockito.doReturn(maternityConfiguration).when(maternityLibrary).getMaternityConfiguration();
        Mockito.doReturn(maternityMetadata).when(maternityConfiguration).getMaternityMetadata();
        Mockito.doReturn(BaseMaternityFormActivity.class).when(maternityMetadata).getMaternityFormActivity();

        JSONObject jsonForm = new JSONObject();
        jsonForm.put("step1", new JSONObject());
        jsonForm.put("step2", new JSONObject());
        jsonForm.put("step3", new JSONObject());

        jsonForm.put(MaternityJsonFormUtils.ENCOUNTER_TYPE, MaternityConstants.EventType.MATERNITY_OUTCOME);

        HashMap<String, String> parcelableData = new HashMap<>();
        String baseEntityId = "89283-23dsd-23sdf";
        parcelableData.put(MaternityConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);

        Intent actualResult = MaternityUtils.buildFormActivityIntent(jsonForm, parcelableData, Mockito.mock(Context.class));
        Form form = (Form) actualResult.getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM);

        assertTrue(form.isWizard());
        assertEquals(MaternityConstants.EventType.MATERNITY_OUTCOME, form.getName());
        assertEquals(baseEntityId, actualResult.getStringExtra(MaternityConstants.IntentKey.BASE_ENTITY_ID));
    }

    @Test
    public void setTextAsHtmlShouldVerifyNougatAndOnwards() {

        TextView textView = mock(TextView.class);
        String html = "";

        MaternityUtils.setTextAsHtml(textView, html);

        verify(textView, Mockito.times(1)).setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
    }

    @Test
    public void contextShouldNotNull() {

        org.smartregister.Context context = MaternityUtils.context();
        assertNotNull(context);
    }

    @Test
    public void getJsonFormToJsonObjectShouldNotNull() {

        String formName = "";
        JSONObject jsonObject = MaternityUtils.getJsonFormToJsonObject(formName);
        assertNull(jsonObject);
    }

    @Test
    public void generateFieldsFromJsonFormShouldVerify() throws Exception {
        String jsonString = "{\"step1\":{\"fields\":[{\"key\":\"gravidity\"}]}}";
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = MaternityUtils.generateFieldsFromJsonForm(jsonObject);
        assertEquals("{\"key\":\"gravidity\"}", jsonArray.get(0).toString());
    }

    @Test
    public void buildRepeatingGroupValuesShouldVerify() throws Exception {

        String stepJsonString = "{\"title\":\"Child's Status\",\"fields\":[{\"key\":\"child_status\",\"value\":[{\"key\":\"baby_age_3s3w323442\",\"value\":\"2\"}]}]}";
        JSONObject stepJsonObject = new JSONObject(stepJsonString);
        String fieldName = "child_status";
        HashMap<String, HashMap<String, String>> groups = MaternityUtils.buildRepeatingGroupValues(stepJsonObject, fieldName);
        assertEquals(0, groups.size());
    }

}
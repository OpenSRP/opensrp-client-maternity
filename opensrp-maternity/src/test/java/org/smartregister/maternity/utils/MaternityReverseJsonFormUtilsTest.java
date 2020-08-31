package org.smartregister.maternity.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.maternity.BaseRobolectricUnitTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityFormActivity;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.sync.CloudantDataHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaternityReverseJsonFormUtilsTest extends BaseRobolectricUnitTest {

    @Mock
    private MaternityLibrary maternityLibrary;


    @Mock
    private MaternityConfiguration maternityConfiguration;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private CloudantDataHandler cloudantDataHandler;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context opensrpContext;

    @Mock
    private ImageRepository imageRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPrepareJsonEditMaternityRegistrationForm() throws JSONException {

        String baseEntityId = "234324-erw432e";

        Map<String, String> detailsMap = new HashMap<>();
        detailsMap.put("first_name", "John");
        detailsMap.put("last_name", "Doe");
        detailsMap.put(MaternityConstants.JSON_FORM_KEY.ENTITY_ID, baseEntityId);
        detailsMap.put(MaternityConstants.KEY.BASE_ENTITY_ID, baseEntityId);


        MaternityMetadata maternityMetadata = new MaternityMetadata(MaternityConstants.Form.MATERNITY_REGISTRATION
                , "table-name"
                , MaternityConstants.EventType.MATERNITY_REGISTRATION
                , MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION
                , "config"
                , BaseMaternityFormActivity.class
                , BaseMaternityProfileActivity.class
                , false);

        Mockito.doReturn(null).when(imageRepository).findByEntityId(baseEntityId);

        Mockito.doReturn(imageRepository).when(opensrpContext).imageRepository();

        Mockito.doReturn(maternityMetadata).when(maternityConfiguration).getMaternityMetadata();

        Mockito.doReturn("Location A").when(allSharedPreferences).fetchCurrentLocality();

        Mockito.doReturn(maternityConfiguration).when(maternityLibrary).getMaternityConfiguration();

        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();

        Mockito.doReturn(opensrpContext).when(coreLibrary).context();

        ReflectionHelpers.setStaticField(CloudantDataHandler.class, "instance", cloudantDataHandler);

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);

        String strForm = MaternityReverseJsonFormUtils
                .prepareJsonEditMaternityRegistrationForm(
                        detailsMap,
                        new ArrayList<>(),
                        RuntimeEnvironment.application.getBaseContext());

        JSONObject form = new JSONObject(strForm);
        JSONArray fields = FormUtils.getMultiStepFormFields(form);

        JSONObject jsonFirstNameObject = FormUtils.getFieldJSONObject(fields, "first_name");

        JSONObject jsonLastNameObject = FormUtils.getFieldJSONObject(fields, "last_name");

        Assert.assertEquals(detailsMap.get("first_name"), jsonFirstNameObject.optString(JsonFormConstants.VALUE));
        Assert.assertEquals(detailsMap.get("last_name"), jsonLastNameObject.optString(JsonFormConstants.VALUE));
        Assert.assertEquals(baseEntityId, form.optString(MaternityConstants.JSON_FORM_KEY.ENTITY_ID));
    }
}
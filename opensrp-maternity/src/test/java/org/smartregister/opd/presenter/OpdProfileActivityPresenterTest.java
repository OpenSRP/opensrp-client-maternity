package org.smartregister.opd.presenter;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-18
 */
@RunWith(RobolectricTestRunner.class)
public class OpdProfileActivityPresenterTest extends BaseTest {

    private OpdProfileActivityPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private OpdProfileActivityContract.View view;
    private OpdProfileActivityContract.Interactor interactor;

    @Before
    public void setUp() throws Exception {
        presenter = Mockito.spy(new OpdProfileActivityPresenter(view));
        interactor = Mockito.spy((OpdProfileActivityContract.Interactor) ReflectionHelpers.getField(presenter, "mProfileInteractor"));

        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);
    }

    @Test
    public void onDestroyWhenNotChangingConfigurationShouldCallInteractorOnDestoryNullifyInteractorIfInteractorIsNotNull() {
        presenter.onDestroy(false);

        Mockito.verify(interactor, Mockito.times(1)).onDestroy(Mockito.eq(false));
        Assert.assertNull(ReflectionHelpers.getField(presenter, "mProfileInteractor"));
    }

    @Test
    public void getProfileViewShouldReturnNullIfTheWeakReferenceObjectIsNull() {
        ReflectionHelpers.setField(presenter, "mProfileView", null);
        Assert.assertNull(presenter.getProfileView());
    }

    @Test
    public void onRegistrationSavedShouldCallViewHideProgressDialog() {
        presenter.onRegistrationSaved(null, false);
        Mockito.verify(view, Mockito.times(1)).hideProgressDialog();
    }

    @Test
    public void onFetchedSavedDiagnosisAndTreatmentFormShouldCallStartFormActivityWithEmptyFormWhenSavedFormIsNull() throws JSONException {
        ArgumentCaptor<JSONObject> formCaptor = ArgumentCaptor.forClass(JSONObject.class);
        JSONObject form = new JSONObject();
        form.put("value", "");
        form.put("question", "What is happening?");

        ReflectionHelpers.setField(presenter, "form", form);
        presenter.onFetchedSavedDiagnosisAndTreatmentForm(null, "caseId", "ec_child");
        Mockito.verify(presenter, Mockito.times(1)).startFormActivity(formCaptor.capture(), Mockito.anyString(), Mockito.nullable(String.class));

        Assert.assertEquals("", formCaptor.getValue().get("value"));
    }

    @Test
    public void onFetchedSavedDiagnosisAndTreatmentFormShouldCallStartFormActivityWithPrefilledFormWhenSavedFormIsNotNull() throws JSONException {
        ArgumentCaptor<JSONObject> formCaptor = ArgumentCaptor.forClass(JSONObject.class);
        JSONObject form = new JSONObject();
        form.put("value", "");
        form.put("question", "What is happening?");

        ReflectionHelpers.setField(presenter, "form", form);

        //Pre-filled form
        JSONObject prefilledForm = new JSONObject();
        prefilledForm.put("value", "I Don't Know");
        prefilledForm.put("question", "What is happening?");

        presenter.onFetchedSavedDiagnosisAndTreatmentForm(
                new OpdDiagnosisAndTreatmentForm(8923, "bei", prefilledForm.toString(), "2019-05-01 11:11:11")
                , "caseId"
                , "ec_child");
        Mockito.verify(presenter, Mockito.times(1)).startFormActivity(formCaptor.capture(), Mockito.anyString(), Mockito.nullable(String.class));
        Assert.assertEquals("I Don't Know", formCaptor.getValue().get("value"));
    }

    @Test
    public void refreshProfileTopSectionShouldCallViewPropertySettersWhenProfileViewIsNotNull() {
        HashMap<String, String> client = new HashMap<>();
        String firstName = "John";
        String lastName = "Doe";
        //String clientDob = "1890-02-02";
        String gender = "Male";
        String registerId = "808920380";
        String clientId = "90239ds-4dfsdf-434rdsf";

        client.put(OpdDbConstants.KEY.FIRST_NAME, firstName);
        client.put(OpdDbConstants.KEY.LAST_NAME, lastName);
        client.put(OpdDbConstants.KEY.GENDER, gender);
        client.put(OpdDbConstants.KEY.REGISTER_ID, registerId);
        client.put(OpdDbConstants.KEY.ID, clientId);

        presenter.refreshProfileTopSection(client);

        Mockito.verify(view, Mockito.times(1)).setProfileName(Mockito.eq(firstName + " " + lastName));
        Mockito.verify(view, Mockito.times(1)).setProfileGender(Mockito.eq(gender));
        Mockito.verify(view, Mockito.times(1)).setProfileID(Mockito.eq(registerId));
        Mockito.verify(view, Mockito.times(1)).setProfileImage(Mockito.eq(clientId));
    }

    @Test
    public void startFormShouldCallStartFormActivityWithInjectedClientGenderAndClientEntityTable() {
        String formName = "registration.json";
        String caseId = "90932-dsdf23-2342";
        String entityTable = "ec_client";

        HashMap<String, String> details = new HashMap<>();
        details.put(OpdDbConstants.KEY.GENDER, AllConstants.FEMALE_GENDER);
        details.put(OpdConstants.IntentKey.ENTITY_TABLE, entityTable);

        ArgumentCaptor<HashMap<String, String>> hashMapArgumentCaptor = ArgumentCaptor.forClass(HashMap.class);

        CommonPersonObjectClient client = new CommonPersonObjectClient(caseId, details, "Jane Doe");
        client.setColumnmaps(details);

        Mockito.doNothing().when(presenter).startFormActivity(Mockito.eq(formName), Mockito.eq(caseId), Mockito.eq(entityTable), Mockito.any(HashMap.class));
        presenter.startForm(formName, client);
        Mockito.verify(presenter, Mockito.times(1)).startFormActivity(Mockito.eq(formName), Mockito.eq(caseId), Mockito.eq(entityTable), hashMapArgumentCaptor.capture());

        Assert.assertEquals(AllConstants.FEMALE_GENDER, hashMapArgumentCaptor.getValue().get("patient_gender"));
    }

    @Test
    public void startFormActivityShouldCallProfileInteractorAndFetchSavedDiagnosisAndTreatmentForm() {
        String formName = OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT;
        String caseId = "90932-dsdf23-2342";
        String entityTable = "ec_client";
        String locationId = "location-id";

        HashMap<String, String> injectedValues = new HashMap<>();

        OpdLibrary opdLibrary = Mockito.mock(OpdLibrary.class);
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Context context = Mockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.doReturn(context).when(opdLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();

        // Mock call to OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID)
        Mockito.doReturn(locationId).when(allSharedPreferences).getPreference(Mockito.eq(AllConstants.CURRENT_LOCATION_ID));
        Mockito.doNothing().when(interactor).fetchSavedDiagnosisAndTreatmentForm(Mockito.eq(caseId), Mockito.eq(entityTable));

        presenter.startFormActivity(formName, caseId, entityTable, injectedValues);
        Mockito.verify(interactor, Mockito.times(1)).fetchSavedDiagnosisAndTreatmentForm(Mockito.eq(caseId), Mockito.eq(entityTable));
    }

    @Test
    public void saveVisitOrDiagnosisFormShouldCallOpdLibraryProcessCheckInFormWhenEventTypeIsCheckIn() throws JSONException {
        String jsonString = "{}";

        OpdLibrary opdLibrary = Mockito.mock(OpdLibrary.class);
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(opdLibrary).processOpdCheckInForm(Mockito.anyString(), Mockito.eq(jsonString), Mockito.nullable(Intent.class));

        Intent intent = new Intent();
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonString);

        presenter.saveVisitOrDiagnosisForm(OpdConstants.EventType.CHECK_IN, intent);

        Mockito.verify(opdLibrary, Mockito.times(1)).processOpdCheckInForm(Mockito.eq(
                OpdConstants.EventType.CHECK_IN)
                , Mockito.eq(jsonString)
                , Mockito.any(Intent.class));
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void saveVisitOrDiagnosisFormShouldCallOpdLibraryProcessDiagnosisFormWhenEventTypeIsDiagnosisAndTreat() throws JSONException {
        String jsonString = "{}";

        OpdLibrary opdLibrary = Mockito.mock(OpdLibrary.class);
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        }).when(opdLibrary).processOpdDiagnosisAndTreatmentForm(Mockito.anyString(), Mockito.nullable(Intent.class));

        Intent intent = new Intent();
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonString);

        presenter.saveVisitOrDiagnosisForm(OpdConstants.EventType.DIAGNOSIS_AND_TREAT, intent);

        Mockito.verify(opdLibrary, Mockito.times(1)).processOpdDiagnosisAndTreatmentForm(Mockito.eq(jsonString)
                , Mockito.any(Intent.class));
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }
}
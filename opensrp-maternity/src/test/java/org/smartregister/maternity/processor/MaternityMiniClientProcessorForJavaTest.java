package org.smartregister.maternity.processor;


import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.CheckInEventProcessException;
import org.smartregister.maternity.pojos.OpdDiagnosis;
import org.smartregister.maternity.pojos.OpdServiceDetail;
import org.smartregister.maternity.pojos.OpdTestConducted;
import org.smartregister.maternity.pojos.OpdTreatment;
import org.smartregister.maternity.pojos.OpdVisit;
import org.smartregister.maternity.repository.OpdDiagnosisRepository;
import org.smartregister.maternity.repository.OpdServiceDetailRepository;
import org.smartregister.maternity.repository.OpdTestConductedRepository;
import org.smartregister.maternity.repository.OpdTreatmentRepository;
import org.smartregister.maternity.repository.OpdVisitRepository;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MaternityLibrary.class)
public class MaternityMiniClientProcessorForJavaTest extends BaseTest {

    private MaternityMiniClientProcessorForJava maternityMiniClientProcessorForJava;

    @Mock
    private MaternityLibrary maternityLibrary;

    @Mock
    private OpdServiceDetailRepository opdServiceDetailRepository;

    @Mock
    private OpdTreatmentRepository opdTreatmentRepository;

    @Mock
    private OpdTestConductedRepository opdTestConductedRepository;

    @Mock
    private OpdDiagnosisRepository opdDiagnosisRepository;

    @Captor
    private ArgumentCaptor<OpdServiceDetail> opdServiceDetailArgumentCaptor;

    @Captor
    private ArgumentCaptor<OpdTreatment> opdTreatmentArgumentCaptor;

    @Captor
    private ArgumentCaptor<OpdDiagnosis> opdDiagnosisArgumentCaptor;

    @Captor
    private ArgumentCaptor<OpdTestConducted> opdTestConductedArgumentCaptor;

    private Event event;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        maternityMiniClientProcessorForJava = Mockito.spy(new MaternityMiniClientProcessorForJava(Mockito.mock(Context.class)));
        event = new Event();
        event.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_ID, "visitId");
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void processServiceDetail() throws Exception {
        PowerMockito.mockStatic(MaternityLibrary.class);
        PowerMockito.when(MaternityLibrary.getInstance()).thenReturn(maternityLibrary);
        PowerMockito.when(maternityLibrary.getOpdServiceDetailRepository()).thenReturn(opdServiceDetailRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.SERVICE_FEE);
        obs.setValue("fee");
        obs.setFieldDataType("text");
        obs.setFieldCode(MaternityConstants.JSON_FORM_KEY.SERVICE_FEE);
        event.addObs(obs);
        event.addDetails(MaternityConstants.JSON_FORM_KEY.ID, "id");

        Whitebox.invokeMethod(maternityMiniClientProcessorForJava, "processServiceDetail", event);
        Mockito.verify(opdServiceDetailRepository, Mockito.times(1)).saveOrUpdate(opdServiceDetailArgumentCaptor.capture());
        Assert.assertEquals("fee", opdServiceDetailArgumentCaptor.getValue().getFee());
        Assert.assertEquals("visitId", opdServiceDetailArgumentCaptor.getValue().getVisitId());
        Assert.assertNotNull(opdServiceDetailArgumentCaptor.getValue().getId());
        Assert.assertNotNull(opdServiceDetailArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdServiceDetailArgumentCaptor.getValue().getCreatedAt());
        Assert.assertEquals(event.getDetails().toString(), opdServiceDetailArgumentCaptor.getValue().getDetails());
    }

    @Test
    public void processTreatment() throws Exception {
        PowerMockito.mockStatic(MaternityLibrary.class);
        PowerMockito.when(MaternityLibrary.getInstance()).thenReturn(maternityLibrary);
        PowerMockito.when(maternityLibrary.getOpdTreatmentRepository()).thenReturn(opdTreatmentRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.MEDICINE);
       obs.setValue("");

        ArrayList<Object> humanReadableValues = new ArrayList<>();
        humanReadableValues.add("Bacteria Killer");

        obs.setHumanReadableValues(humanReadableValues);
        obs.setFieldDataType("text");
        obs.setFieldCode(MaternityConstants.JSON_FORM_KEY.MEDICINE);
        event.addObs(obs);
        event.addDetails(MaternityConstants.JSON_FORM_KEY.ID, "id");
        event.addDetails(MaternityConstants.KEY.VALUE, "[{\"key\":\"BB009900\",\"text\":\"Bacteria Killer\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"property\":{\"pack_size\":\"BB009900\",\"product_code\":\"BB009900\",\"dispensing_unit\":\"dispensingUnit:Each\",\"meta\":{\"duration\":\"10 days\",\"dosage\":\"2x2\",\"info\":\"Dose: 10 days, Duration: 2x2\"}}}]");

        Whitebox.invokeMethod(maternityMiniClientProcessorForJava, "processTreatment", event);
        Mockito.verify(opdTreatmentRepository, Mockito.times(1)).saveOrUpdate(opdTreatmentArgumentCaptor.capture());
        Assert.assertEquals("10 days", opdTreatmentArgumentCaptor.getValue().getDuration());
        Assert.assertEquals("2x2", opdTreatmentArgumentCaptor.getValue().getDosage());
        Assert.assertEquals("Bacteria Killer", opdTreatmentArgumentCaptor.getValue().getMedicine());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getId());
    }

    @Test
    public void processDiagnosis() throws Exception {
        PowerMockito.mockStatic(MaternityLibrary.class);
        PowerMockito.when(MaternityLibrary.getInstance()).thenReturn(maternityLibrary);
        PowerMockito.when(maternityLibrary.getOpdDiagnosisRepository()).thenReturn(opdDiagnosisRepository);

        String bacterial_meningitis = "Bacterial Meningitis";

        Obs obs = new Obs();
        obs.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.DISEASE_CODE);
        obs.setValue("");
        obs.setFieldDataType("text");

        ArrayList<String> humanReadableValues = new ArrayList<>();
        humanReadableValues.add(bacterial_meningitis);
        obs.addToHumanReadableValuesList(humanReadableValues);

        event.addObs(obs);
        event.addDetails(MaternityConstants.JSON_FORM_KEY.ID, "id");
        event.addDetails(MaternityConstants.KEY.VALUE, "[{\"key\":\"code_17d\",\"text\":\"Bacterial Meningitis\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"property\":{\"presumed-id\":\"\",\"code\":\"code_17d\",\"confirmed-id\":\"\"}}]");

        Obs obs1 = new Obs();
        obs1.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.DIAGNOSIS);
        obs1.setValue("Patient has Bacterial Meningitis");
        obs1.setFieldDataType("text");
        obs1.setFieldCode(MaternityConstants.JSON_FORM_KEY.DIAGNOSIS);
        event.addObs(obs1);

        Obs obs2 = new Obs();
        obs2.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
        obs2.setValue("Confirmed");
        obs2.setFieldDataType("text");
        obs2.setFieldCode(MaternityConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
        event.addObs(obs2);

        Whitebox.invokeMethod(maternityMiniClientProcessorForJava, "processDiagnosis", event);
        Mockito.verify(opdDiagnosisRepository, Mockito.times(1)).saveOrUpdate(opdDiagnosisArgumentCaptor.capture());
        Assert.assertEquals("Patient has Bacterial Meningitis", opdDiagnosisArgumentCaptor.getValue().getDiagnosis());
        Assert.assertEquals(bacterial_meningitis, opdDiagnosisArgumentCaptor.getValue().getDisease());
        Assert.assertEquals("Confirmed", opdDiagnosisArgumentCaptor.getValue().getType());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getId());
    }


    @Test
    public void processTestConducted() throws Exception {
        PowerMockito.mockStatic(MaternityLibrary.class);
        PowerMockito.when(MaternityLibrary.getInstance()).thenReturn(maternityLibrary);
        PowerMockito.when(maternityLibrary.getOpdTestConductedRepository()).thenReturn(opdTestConductedRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER);
        obs.setValue("diagnostic test result");
        obs.setFieldDataType("text");
        obs.setFieldCode(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER);
        event.addObs(obs);

        Obs obs1 = new Obs();
        obs1.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST);
        obs1.setValue("diagnostic test");
        obs1.setFieldDataType("text");
        obs1.setFieldCode(MaternityConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST);
        event.addObs(obs1);
        event.addDetails(MaternityConstants.JSON_FORM_KEY.ID, "id");

        Whitebox.invokeMethod(maternityMiniClientProcessorForJava, "processTestConducted", event);

        Mockito.verify(opdTestConductedRepository, Mockito.times(1)).saveOrUpdate(opdTestConductedArgumentCaptor.capture());
        Assert.assertEquals("diagnostic test result", opdTestConductedArgumentCaptor.getValue().getResult());
        Assert.assertEquals("diagnostic test", opdTestConductedArgumentCaptor.getValue().getTest());
        Assert.assertEquals("visitId", opdTestConductedArgumentCaptor.getValue().getVisitId());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getId());
    }

    @Test
    public void getEventTypesShouldReturnAtLeast6EventTypesAllStartingWithOpd() {
        HashSet<String> eventTypes = maternityMiniClientProcessorForJava.getEventTypes();

        Assert.assertTrue(eventTypes.size() >= 6);
        for (String eventType: eventTypes) {
            Assert.assertTrue(eventType.startsWith("OPD"));
        }
    }

    @Test
    public void processEventClientShouldCallProcessCheckInWhenEvenTypeIsOPDCheckIn() throws Exception {
        String formSubmissionId = "submission-id";
        Event event = new Event().withEventType(MaternityConstants.EventType.CHECK_IN).withFormSubmissionId(formSubmissionId);
        event.addDetails("d", "d");
        event.setEventDate(new DateTime());

        EventClient eventClient = new EventClient(event, new Client("base-entity-id"));

        Mockito.doNothing()
                .when(maternityMiniClientProcessorForJava)
                .processCheckIn(Mockito.any(Event.class), Mockito.any(Client.class));

        // Mock CoreLibrary calls to make they pass
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        org.smartregister.Context contextMock = Mockito.mock(org.smartregister.Context.class);
        Mockito.doReturn(contextMock)
                .when(coreLibrary)
                .context();
        EventClientRepository eventClientRepository = Mockito.mock(EventClientRepository.class);
        Mockito.doReturn(eventClientRepository)
                .when(contextMock)
                .getEventClientRepository();

        maternityMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<Event>(), null);

        Mockito.verify(maternityMiniClientProcessorForJava, Mockito.times(1))
                .processCheckIn(Mockito.any(Event.class), Mockito.any(Client.class));
        Mockito.verify(eventClientRepository, Mockito.times(1))
                .markEventAsProcessed(Mockito.eq(formSubmissionId));

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void processEventClientShouldThrowExceptionWhenClientIsNull() throws Exception {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Could not process this OPD Check-In Event because Client bei referenced by OPD Check-In event does not exist");

        Event event = new Event().withEventType(MaternityConstants.EventType.CHECK_IN).withBaseEntityId("bei");
        event.addDetails("d", "d");

        EventClient eventClient = new EventClient(event, null);

        maternityMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<Event>(), null);
    }

    @Test
    public void processCheckInShouldThrowExceptionWhenVisitIdIsNull() throws CheckInEventProcessException {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Check-in of event");

        String baseEntityId = "bei";

        Event event = new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(new DateTime());
        event.addDetails("d", "d");

        Client client = new Client(baseEntityId);

        maternityMiniClientProcessorForJava.processCheckIn(event, client);
    }

    @Test
    public void processCheckInShouldThrowExceptionSavingVisitFails() throws CheckInEventProcessException {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Could not process this OPD Check-In Event because Visit with id visit-id could not be saved in the db. Fail operation failed");

        String baseEntityId = "bei";

        Event event = new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(new DateTime());
        event.addDetails("visitId", "visit-id");
        event.addDetails("visitDate", "2018-03-03 10:10:10");

        Client client = new Client(baseEntityId);

        //Mock MaternityLibrary
        MaternityLibrary maternityLibrary = Mockito.mock(MaternityLibrary.class);
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        OpdVisitRepository opdVisitRepository = Mockito.mock(OpdVisitRepository.class);
        Mockito.doReturn(opdVisitRepository).when(maternityLibrary).getVisitRepository();

        // Mock Repository
        Repository repository = Mockito.mock(Repository.class);
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(repository).when(maternityLibrary).getRepository();
        Mockito.doReturn(database).when(repository).getWritableDatabase();

        // Mock OpdVisitRepository to return false
        Mockito.doReturn(false).when(opdVisitRepository).addVisit(Mockito.any(OpdVisit.class));

        maternityMiniClientProcessorForJava.processCheckIn(event, client);
    }
}

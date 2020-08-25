package org.smartregister.maternity.processor;


import android.content.Context;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.MaternityCloseEventProcessException;
import org.smartregister.maternity.pojo.MaternityChild;
import org.smartregister.maternity.repository.MaternityChildRepository;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@RunWith(PowerMockRunner.class)
public class MaternityMiniClientProcessorForJavaTest extends BaseTest {

    private MaternityMiniClientProcessorForJava maternityMiniClientProcessorForJava;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private MaternityLibrary maternityLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        maternityMiniClientProcessorForJava = Mockito.spy(new MaternityMiniClientProcessorForJava(Mockito.mock(Context.class)));
        Event event = new Event();
        event.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_ID, "visitId");
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void getEventTypeShouldVerifyTheSize() {
        HashSet<String> eventTypes = maternityMiniClientProcessorForJava.getEventTypes();

        Assert.assertEquals(6, eventTypes.size());
    }

    @Test
    public void processEventClientShouldThrowExceptionWhenClientIsNull() throws Exception {
        expectedException.expect(MaternityCloseEventProcessException.class);
        expectedException.expectMessage("Could not process this Maternity Close Event because Client bei referenced by Maternity Close event does not exist");

        Event event = new Event().withEventType(MaternityConstants.EventType.MATERNITY_CLOSE).withBaseEntityId("bei");
        event.addDetails("d", "d");

        EventClient eventClient = new EventClient(event, null);

        maternityMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<Event>(), null);
    }

    @Test
    public void testProcessBabiesBornShouldCallRepositoryIfBaseEntityIdExists() throws Exception {
        String babiesBorn = "{\"3b562659b3f64f998dccfae199f7ea0d\":" +
                "{\"baby_care_mgt\":\"[\\\"kmc\\\",\\\"antibiotics\\\"]\",\"apgar\":\"10\",\"base_entity_id\":\"2323-2323-sds\",\"child_hiv_status\":\"Exposed\",\"nvp_administration\":\"Yes\",\"baby_first_cry\":\"Yes\",\"baby_complications\":\"[\\\"premature\\\",\\\"asphyxia\\\"]\",\"baby_first_name\":\"Nameless\",\"baby_last_name\":\"Master\",\"baby_dob\":\"03-06-2020\",\"discharged_alive\":\"Yes\",\"birth_weight_entered\":\"2300\",\"birth_height_entered\":\"54\",\"baby_gender\":\"Male\",\"bf_first_hour\":\"Yes\"}}";
        MaternityChildRepository maternityChildRepositorySpy = Mockito.spy(new MaternityChildRepository());
        Mockito.doReturn(maternityChildRepositorySpy).when(maternityLibrary).getMaternityChildRepository();
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        Mockito.doReturn(true).when(maternityChildRepositorySpy).saveOrUpdate(Mockito.any(MaternityChild.class));
        Event event = new Event();
        event.setBaseEntityId("232-wew3-23");
        event.setEventDate(new DateTime());
        Whitebox.invokeMethod(maternityMiniClientProcessorForJava,
                "processBabiesBorn", babiesBorn,
                event);

        Mockito.verify(maternityChildRepositorySpy, Mockito.times(1))
                .saveOrUpdate(Mockito.any(MaternityChild.class));
    }

    @Test
    public void testProcessStillBornShouldCallRepositoryIfMotherBaseEntityIdExists() throws Exception {
        String stillBorn = "{\"cabdeffcdca64a63800c8718f94d72ee\":{\"stillbirth_condition\":\"Fresh\"},\"70bb07814ded40a59f1346928909d134\":{\"stillbirth_condition\":\"Macerated\"}}";
        MaternityChildRepository maternityChildRepositorySpy = Mockito.spy(new MaternityChildRepository());
        Mockito.doReturn(maternityChildRepositorySpy).when(maternityLibrary).getMaternityChildRepository();
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        Mockito.doReturn(true).when(maternityChildRepositorySpy).saveOrUpdate(Mockito.any(MaternityChild.class));
        Event event = new Event();
        event.setBaseEntityId("232-wew3-23");
        event.setEventDate(new DateTime());
        Whitebox.invokeMethod(maternityMiniClientProcessorForJava,
                "processStillBorn", stillBorn,
                event);

        Mockito.verify(maternityChildRepositorySpy, Mockito.times(2))
                .saveOrUpdate(Mockito.any(MaternityChild.class));
    }

    @Test
    public void testGenerateKeyValuesFromEventShouldFillMapCorrectly() throws Exception {
        Event event = new Event();

        Obs obs1 = new Obs();
        obs1.setHumanReadableValue("one");
        obs1.setFormSubmissionField("count1");

        Obs obs2 = new Obs();
        obs2.setHumanReadableValues(new ArrayList<>());
        obs2.setValues(Arrays.asList("huma", "man"));
        obs2.setFormSubmissionField("count2");

        event.addObs(obs1);
        event.addObs(obs2);

        HashMap<String, String> map = new HashMap<>();

        Whitebox.invokeMethod(maternityMiniClientProcessorForJava,
                "generateKeyValuesFromEvent", event,
                map);

        Assert.assertEquals(2, map.size());
    }
}

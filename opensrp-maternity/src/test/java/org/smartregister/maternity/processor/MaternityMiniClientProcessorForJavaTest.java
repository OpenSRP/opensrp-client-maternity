package org.smartregister.maternity.processor;


import android.content.Context;

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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.CheckInEventProcessException;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.ArrayList;
import java.util.HashSet;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MaternityLibrary.class)
public class MaternityMiniClientProcessorForJavaTest extends BaseTest {

    private MaternityMiniClientProcessorForJava maternityMiniClientProcessorForJava;

    @Mock
    private MaternityLibrary maternityLibrary;

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
        Obs obs = new Obs();
        obs.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.SERVICE_FEE);
        obs.setValue("fee");
        obs.setFieldDataType("text");
        obs.setFieldCode(MaternityConstants.JSON_FORM_KEY.SERVICE_FEE);
        event.addObs(obs);
        event.addDetails(MaternityConstants.JSON_FORM_KEY.ID, "id");

        Whitebox.invokeMethod(maternityMiniClientProcessorForJava, "processServiceDetail", event);
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
    public void processEventClientShouldThrowExceptionWhenClientIsNull() throws Exception {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Could not process this OPD Check-In Event because Client bei referenced by OPD Check-In event does not exist");

        Event event = new Event().withEventType(MaternityConstants.EventType.CHECK_IN).withBaseEntityId("bei");
        event.addDetails("d", "d");

        EventClient eventClient = new EventClient(event, null);

        maternityMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<Event>(), null);
    }
}

package org.smartregister.maternity.processor;


import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.MaternityCloseEventProcessException;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.ArrayList;
import java.util.HashSet;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MaternityLibrary.class)
public class MaternityMiniClientProcessorForJavaTest extends BaseTest {

    private MaternityMiniClientProcessorForJava maternityMiniClientProcessorForJava;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void getEventTypesShouldReturnAtLeast6EventTypesAllStartingWithMaternity() {
        HashSet<String> eventTypes = maternityMiniClientProcessorForJava.getEventTypes();

        Assert.assertEquals(4, eventTypes.size());
        for (String eventType: eventTypes) {
            Assert.assertTrue(eventType.contains("Maternity"));
        }
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
}

package org.smartregister.maternity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.shadows.ShadowMaternityLibrary;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowMaternityLibrary.class})
public class MaternityLibraryTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void initShouldCreateNewLibraryInstanceWhenInstanceIsNull() {
        assertNull(ReflectionHelpers.getStaticField(MaternityLibrary.class, "instance"));

        MaternityLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(ReflectionHelpers.getStaticField(MaternityLibrary.class, "instance"));
    }

    @Test
    public void getInstanceShouldThrowIllegalStateException() throws Throwable {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Instance does not exist!!! Call org.smartregister.maternity.MaternityLibrary"
                + ".init method in the onCreate method of "
                + "your Application class");

        MaternityLibrary.getInstance();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }

    @Test
    public void getMaternityRulesEngineHelperShouldReturnNonNull() {
        MaternityLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(MaternityLibrary.getInstance().getMaternityRulesEngineHelper());
    }

    @Test
    public void getLatestValidCheckInDateShouldReturn1DayFromNow() {
        MaternityLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);

        long timeNow = new Date().getTime();

        assertEquals(timeNow - 24 * 60 * 60 * 1000, MaternityLibrary.getInstance().getLatestValidCheckInDate().getTime(), 100);
    }


//TODO: Fix Robolectric not mocking calendar & date class using shadow class and then these two tests are going to be feasible

    @Test
    public void isPatientInTreatedStateShouldReturnTrueWhenCurrentDateIsBeforeMidnightOfTreatmentDate() throws ParseException {
        MaternityLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MaternityDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-04 20:23:20");

        ShadowMaternityLibrary.setMockedTime(date.getTime());
        Assert.assertTrue(MaternityLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }

    @Test
    public void isPatientInTreatedStateShouldReturnFalseWhenCurrentDateIsAfterMidnightOfTreatmentDate() throws ParseException {
        MaternityLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(MaternityConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MaternityDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-05 20:23:20");

        ShadowMaternityLibrary.setMockedTime(date.getTime());
        Assert.assertFalse(MaternityLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }
}
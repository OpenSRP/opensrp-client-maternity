package org.smartregister.opd.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.opd.BuildConfig;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpdLibrary.class)
public class OpdLookUpUtilsTest {

    @Mock
    private OpdLibrary opdLibrary;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLookUpQuery() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.opdLookUpQuery()).thenReturn("");

        Map<String, String> entityMap = new HashMap<>();
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "lookUpQuery", entityMap);
        Assert.assertEquals(";", result);
    }

    @Test
    public void testGetMainConditionStringWhenEntityMapIsEmpty() throws Exception {
        Map<String, String> entityMap = new HashMap<>();
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "getMainConditionString", entityMap);
        Assert.assertEquals("", result);
    }

    @Test
    public void testGetMainConditionStringWhenEntityMapIsWithValue() throws Exception {
        String firstName = "first_name";
        String lastName = "last_name";
        String bht_id = "bht_mid";
        String national_id = "national_id";
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put(firstName, "");
        entityMap.put(lastName, "");
        entityMap.put(bht_id, "");
        entityMap.put(national_id, "");
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "getMainConditionString", entityMap);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testClientLookUpWhenContextIsNull() throws Exception {
        Map<String, String> entityLookUp = new HashMap<>();
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", (Object) null, entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testClientLookUpWhenMapIsEmpty() throws Exception {
        Map<String, String> entityLookUp = new HashMap<>();
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", PowerMockito.mock(Context.class), entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testClientLookUpWhenMapIsNotEmptyAndContextIsNotNullWithTable() throws Exception {
        OpdLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), PowerMockito.mock(OpdConfiguration.class),
                BuildConfig.VERSION_CODE, 1);
        Map<String, String> entityLookUp = new HashMap<>();
        entityLookUp.put("first_name", "");
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", PowerMockito.mock(Context.class), entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(expectedResult, result);
    }

    @After
    public void tearDown(){
        ReflectionHelpers.setStaticField(OpdLibrary.class,"instance", null);
    }

}
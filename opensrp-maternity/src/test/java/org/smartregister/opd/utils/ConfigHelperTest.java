package org.smartregister.opd.utils;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-19
 */

public class ConfigHelperTest {

    @Test
    public void defaultRegisterConfigurationShouldNullWhenContextIsNull() {
        assertNull(ConfigHelper.defaultRegisterConfiguration(null));
    }

    @Test
    public void defaultRegisterConfigurationShouldReturn() {
        RegisterConfiguration registerConfiguration = ConfigHelper.defaultRegisterConfiguration(Mockito.mock(Context.class));
        assertFalse(registerConfiguration.isEnableAdvancedSearch());
        assertFalse(registerConfiguration.isEnableSortList());
        assertFalse(registerConfiguration.isEnableSortList());
        assertFalse(registerConfiguration.isEnableJsonViews());

        assertEquals(0, registerConfiguration.getFilterFields().size());
        assertEquals(0, registerConfiguration.getSortFields().size());
    }
}
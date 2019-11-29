package org.smartregister.opd.utils;

import org.jeasy.rules.api.Facts;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-15
 */

public class OpdFactsUtilTest {

    @Test
    public void putNonNullFactShouldCallFactPutIfNotNUll() {
        String factKey = "onaio";
        String value = "company";
        Facts facts = new Facts();

        assertNull(facts.get(factKey));
        OpdFactsUtil.putNonNullFact(facts, factKey, value);

        assertEquals(value, facts.get(factKey));
    }
}
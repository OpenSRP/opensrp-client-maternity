package org.smartregister.maternity.utils;

import org.jeasy.rules.api.Facts;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityFactsUtilTest {

    @Test
    public void putNonNullFactShouldCallFactPutIfNotNUll() {
        String factKey = "onaio";
        String value = "company";
        Facts facts = new Facts();

        assertNull(facts.get(factKey));
        MaternityFactsUtil.putNonNullFact(facts, factKey, value);

        assertEquals(value, facts.get(factKey));
    }
}
package org.smartregister.maternity.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jeasy.rules.api.Facts;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-11
 */

public class MaternityFactsUtil {

    public static void putNonNullFact(@NonNull Facts facts, @NonNull String factKey, @Nullable Object factValue) {
        if (factValue != null) {
            facts.put(factKey, factValue);
        }
    }
}

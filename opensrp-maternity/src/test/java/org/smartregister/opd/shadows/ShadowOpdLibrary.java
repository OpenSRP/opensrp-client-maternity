package org.smartregister.opd.shadows;

import android.support.annotation.NonNull;

import org.robolectric.annotation.Implements;
import org.smartregister.opd.OpdLibrary;

import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@Implements(OpdLibrary.class)
public class ShadowOpdLibrary {

    public static long currentTime;
    public static boolean isTimeSetExplicitly;

    public static void setMockedTime(long time) {
        currentTime = time;
        isTimeSetExplicitly = true;
    }

    @NonNull
    protected Date getDateNow() {
        if (isTimeSetExplicitly) {
            return new Date(currentTime);
        } else {
            return new Date();
        }
    }
}


package org.smartregister.maternity.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
@Implements(Date.class)
public class ShadowDate {

    public static long currentTime;
    public static boolean isTimeSetExplicitly;

    @RealObject
    private Date realDate;

    public static void setMockedTime(long time) {
        currentTime = time;
        isTimeSetExplicitly = true;
    }

    @Implementation
    public void __constructor__() {
        if (isTimeSetExplicitly) {
            realDate.setTime(currentTime);
        } else {
            realDate.setTime(Calendar.getInstance().getTime().getTime());
        }
    }

    @Implementation
    public void setTime(long time) {
        if (isTimeSetExplicitly) {
            realDate.setTime(currentTime);
        } else {
            realDate.setTime(Calendar.getInstance().getTime().getTime());
        }
    }

}

package org.smartregister.opd.exception;

import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-14
 */

public class CheckInEventProcessException extends Exception {

    public CheckInEventProcessException() {
        super("Could not process this OPD Check-In Event");
    }

    public CheckInEventProcessException(@NonNull String message) {
        super("Could not process this OPD Check-In Event because " + message);
    }

}

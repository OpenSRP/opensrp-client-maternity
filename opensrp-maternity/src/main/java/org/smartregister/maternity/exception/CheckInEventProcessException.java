package org.smartregister.maternity.exception;

import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class CheckInEventProcessException extends Exception {

    public CheckInEventProcessException() {
        super("Could not process this MATERNITY Check-In Event");
    }

    public CheckInEventProcessException(@NonNull String message) {
        super("Could not process this MATERNITY Check-In Event because " + message);
    }

}

package org.smartregister.maternity.exception;

import android.support.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityCloseEventProcessException extends Exception {

    public MaternityCloseEventProcessException() {
        super("Could not process this Maternity Close Event");
    }

    public MaternityCloseEventProcessException(@NonNull String message) {
        super("Could not process this Maternity Close Event because " + message);
    }

}

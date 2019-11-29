package org.smartregister.opd.exception;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class NewInstanceException extends RuntimeException {

    public NewInstanceException() {
    }

    public NewInstanceException(String message) {
        super(message);
    }
}

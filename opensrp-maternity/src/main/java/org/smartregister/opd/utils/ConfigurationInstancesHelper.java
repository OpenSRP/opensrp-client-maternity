package org.smartregister.opd.utils;

import android.support.annotation.NonNull;

import org.smartregister.opd.exception.NewInstanceException;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class ConfigurationInstancesHelper {

    @NonNull
    public static <T> T newInstance(Class<T> clas) {
        try {
            return clas.newInstance();
        } catch (IllegalAccessException e) {
            Timber.e(e);
        } catch (InstantiationException e) {
            Timber.e(e);
        }

        throw new NewInstanceException("Could not create a new instance of " + clas.getName());
    }
}

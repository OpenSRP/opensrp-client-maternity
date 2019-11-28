package org.smartregister.maternity.utils;



import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-27
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

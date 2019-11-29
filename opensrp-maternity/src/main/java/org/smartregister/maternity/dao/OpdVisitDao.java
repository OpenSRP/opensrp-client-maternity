package org.smartregister.maternity.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.maternity.pojos.OpdVisit;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface OpdVisitDao {


    @Nullable
    OpdVisit getLatestVisit(@NonNull String clientBaseEntityId);
}

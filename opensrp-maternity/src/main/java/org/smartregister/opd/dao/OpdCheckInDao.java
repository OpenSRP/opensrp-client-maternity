package org.smartregister.opd.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.OpdCheckIn;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public interface OpdCheckInDao {

    @Nullable
    OpdCheckIn getLatestCheckIn(@NonNull String clientBaseEntityId);

    @Nullable
    OpdCheckIn getCheckInByVisit(@NonNull String visitId);

    boolean addCheckIn(@NonNull OpdCheckIn checkIn);

}

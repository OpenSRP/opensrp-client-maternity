package org.smartregister.maternity.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 26-03-2020.
 */
public interface MaternityFormProcessingTask {

    void processMaternityForm(@NonNull String eventType, String jsonString, @Nullable Intent data);
}

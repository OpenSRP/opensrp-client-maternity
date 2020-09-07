package org.smartregister.maternity.listener;

import androidx.annotation.NonNull;

import org.smartregister.maternity.pojo.OngoingTask;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 26-03-2020.
 */
public interface OngoingTaskCompleteListener {

    void onTaskComplete(@NonNull OngoingTask ongoingTask);
}

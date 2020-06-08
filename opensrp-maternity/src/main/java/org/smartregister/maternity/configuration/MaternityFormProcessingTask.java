package org.smartregister.maternity.configuration;

import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 26-03-2020.
 */
public interface MaternityFormProcessingTask<T> {

    T processMaternityForm(String jsonString, @Nullable Intent data) throws JSONException;
}

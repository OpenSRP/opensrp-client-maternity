package org.smartregister.maternity.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityReverseJsonFormUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;

public class FetchRegistrationDataTask extends AsyncTask<String, Void, String> {

    private WeakReference<Context> contextWeakReference;
    private OnTaskComplete onTaskComplete;

    public FetchRegistrationDataTask(@NonNull WeakReference<Context> contextWeakReference, @NonNull OnTaskComplete onTaskComplete) {
        this.contextWeakReference = contextWeakReference;
        this.onTaskComplete = onTaskComplete;
    }

    @Nullable
    protected String doInBackground(String... params) {
        Map<String, String> detailsMap = MaternityLibrary.getInstance().getMaternityRegistrationDetailsRepository().getClientDemographicDetails(params[0]);
        if (detailsMap != null) {
            detailsMap.put(MaternityJsonFormUtils.OPENSRP_ID, detailsMap.get(MaternityConstants.KEY.OPENSRP_ID));
            return MaternityReverseJsonFormUtils.prepareJsonEditMaternityRegistrationForm(detailsMap, Arrays.asList(MaternityJsonFormUtils.OPENSRP_ID, MaternityConstants.JSON_FORM_KEY.BHT_ID), contextWeakReference.get());
        }
        return null;
    }

    protected void onPostExecute(@Nullable String jsonForm) {
        onTaskComplete.onSuccess(jsonForm);
    }

    public interface OnTaskComplete {

        void onSuccess(@Nullable String jsonForm);
    }
}
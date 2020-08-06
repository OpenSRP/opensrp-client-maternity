package org.smartregister.maternity.interactor;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.configuration.MaternityRegisterQueryProviderContract;
import org.smartregister.maternity.contract.MaternityProfileActivityContract;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.MaternityPartialForm;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.maternity.utils.AppExecutors;
import org.smartregister.maternity.utils.ConfigurationInstancesHelper;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class MaternityProfileInteractor implements MaternityProfileActivityContract.Interactor {

    private MaternityProfileActivityContract.Presenter mProfilePresenter;
    private AppExecutors appExecutors;

    public MaternityProfileInteractor(@NonNull MaternityProfileActivityContract.Presenter presenter) {
        this.mProfilePresenter = presenter;
        appExecutors = MaternityLibrary.getInstance().getAppExecutors();
    }

    @Override
    public void fetchSavedPartialForm(@Nullable String formType, @NonNull final String baseEntityId, @NonNull final String entityTable) {
        appExecutors.diskIO().execute(() -> {
            final MaternityPartialForm savedPartialForm = MaternityLibrary
                    .getInstance()
                    .getMaternityPartialFormRepository()
                    .findOne(new MaternityPartialForm(baseEntityId, formType));

            appExecutors.mainThread().execute(() -> {
                if (mProfilePresenter instanceof MaternityProfileActivityContract.InteractorCallBack) {
                    ((MaternityProfileActivityContract.InteractorCallBack) mProfilePresenter)
                            .onFetchedSavedPartialForm(savedPartialForm, baseEntityId, entityTable);
                }
            });
        });
    }

    @Override
    public void saveRegistration(final @NonNull MaternityEventClient maternityEventClient, final @NonNull String jsonString
            , final @NonNull RegisterParams registerParams, final @NonNull MaternityProfileActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(maternityEventClient, jsonString, registerParams);
                final CommonPersonObjectClient client = retrieveUpdatedClient(maternityEventClient.getEvent().getBaseEntityId());


                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(client, registerParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Nullable
    @Override
    public CommonPersonObjectClient retrieveUpdatedClient(@NonNull String baseEntityId) {
        MaternityRegisterQueryProviderContract queryProviderContract = ConfigurationInstancesHelper.newInstance(MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityRegisterQueryProvider());
        String query = queryProviderContract.mainSelectWhereIDsIn();


        String joinedIds = "'" + baseEntityId + "'";
        query = query.replace("%s", joinedIds);

        CommonRepository commonRepository = MaternityLibrary.getInstance().context().commonrepository(MaternityDbConstants.Table.EC_CLIENT);
        Cursor cursor = commonRepository.rawCustomQueryForAdapter(query);

        if (cursor != null && cursor.moveToFirst()) {
            CommonPersonObject commonPersonObject = commonRepository.getCommonPersonObjectFromCursor(cursor);
            String name = commonPersonObject.getDetails().get(MaternityDbConstants.KEY.FIRST_NAME)
                    + " " + commonPersonObject.getDetails().get(MaternityDbConstants.KEY.LAST_NAME);
            CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(),
                    commonPersonObject.getDetails(), name);
            client.setColumnmaps(commonPersonObject.getDetails());

            return client;
        }

        return null;
    }

    private void saveRegistration(@NonNull MaternityEventClient maternityEventClient, @NonNull String jsonString
            , @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();
            try {

                Client baseClient = maternityEventClient.getClient();
                Event baseEvent = maternityEventClient.getEvent();

                if (baseClient != null && params.isEditMode()) {
                    try {
                        MaternityJsonFormUtils.mergeAndSaveClient(baseClient);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                String formSubmissionId = addEvent(params, baseEvent);

                if (formSubmissionId != null) {
                    currentFormSubmissionIds.add(formSubmissionId);
                }

                updateOpenSRPId(jsonString, params, baseClient);
                addImageLocation(jsonString, baseClient, baseEvent);
            } catch (Exception e) {
                Timber.e(e);
            }

            long lastSyncTimeStamp = MaternityLibrary.getInstance().context().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            MaternityLibrary.getInstance().getClientProcessorForJava().processClient(MaternityLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            MaternityLibrary.getInstance().context().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void addImageLocation(@NonNull String jsonString, @Nullable Client baseClient, @Nullable Event baseEvent) {
        if (baseClient != null || baseEvent != null) {
            String imageLocation = MaternityJsonFormUtils.getFieldValue(jsonString, MaternityConstants.KEY.PHOTO);
            if (StringUtils.isNotBlank(imageLocation)) {
                MaternityJsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(@NonNull String jsonString, @NonNull RegisterParams params, @Nullable Client baseClient) {
        if (params.isEditMode() && baseClient != null) {
            // Unassign current OPENSRP ID
            try {
                String newOpenSRPId = baseClient.getIdentifier(MaternityJsonFormUtils.OPENSRP_ID).replace("-", "");
                String currentOpenSRPId = MaternityJsonFormUtils.getString(jsonString, MaternityJsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                if (!newOpenSRPId.equals(currentOpenSRPId)) {
                    //OPENSRP ID was changed
                    MaternityLibrary.getInstance().getUniqueIdRepository().open(currentOpenSRPId);
                }
            } catch (Exception e) {//might crash if M_ZEIR
                Timber.d(e);
            }
        }

    }

    @Nullable
    private String addEvent(RegisterParams params, Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseEvent));
            MaternityLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            return eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString());
        }

        return null;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfilePresenter = null;
        }
    }
}

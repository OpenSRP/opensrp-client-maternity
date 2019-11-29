package org.smartregister.opd.interactor;

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
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.opd.utils.ConfigurationInstancesHelper;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileInteractor implements OpdProfileActivityContract.Interactor {

    private OpdProfileActivityContract.Presenter mProfilePresenter;
    private AppExecutors appExecutors;

    public OpdProfileInteractor(@NonNull OpdProfileActivityContract.Presenter presenter) {
        this.mProfilePresenter = presenter;
        appExecutors = new AppExecutors();
    }

    @Override
    public void fetchSavedDiagnosisAndTreatmentForm(@NonNull final String baseEntityId, @NonNull final String entityTable) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm = OpdLibrary
                        .getInstance()
                        .getOpdDiagnosisAndTreatmentFormRepository()
                        .findOne(new OpdDiagnosisAndTreatmentForm(baseEntityId));

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mProfilePresenter instanceof OpdProfileActivityContract.InteractorCallBack) {
                            ((OpdProfileActivityContract.InteractorCallBack) mProfilePresenter)
                                    .onFetchedSavedDiagnosisAndTreatmentForm(diagnosisAndTreatmentForm, baseEntityId, entityTable);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void saveRegistration(final @NonNull OpdEventClient opdEventClient, final @NonNull String jsonString
            , final @NonNull RegisterParams registerParams, final @NonNull OpdProfileActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(opdEventClient, jsonString, registerParams);
                final CommonPersonObjectClient client = retrieveUpdatedClient(opdEventClient.getEvent().getBaseEntityId());


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
        OpdRegisterQueryProviderContract queryProviderContract = ConfigurationInstancesHelper.newInstance(OpdLibrary.getInstance().getOpdConfiguration().getOpdRegisterQueryProvider());
        String query = queryProviderContract.mainSelectWhereIDsIn();


        String joinedIds = "'" + baseEntityId + "'";
        query = query.replace("%s", joinedIds);

        CommonRepository commonRepository = OpdLibrary.getInstance().context().commonrepository(OpdDbConstants.Table.EC_CLIENT);
        Cursor cursor = commonRepository.rawCustomQueryForAdapter(query);

        if (cursor != null && cursor.moveToFirst()) {
            CommonPersonObject commonPersonObject = commonRepository.getCommonPersonObjectFromCursor(cursor);
            String name = commonPersonObject.getColumnmaps().get(OpdDbConstants.KEY.FIRST_NAME)
                    + " " + commonPersonObject.getColumnmaps().get(OpdDbConstants.KEY.LAST_NAME);
            CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(),
                    commonPersonObject.getDetails(), name);
            client.setColumnmaps(commonPersonObject.getDetails());

            return client;
        }

        return null;
    }

    private void saveRegistration(@NonNull OpdEventClient opdEventClient, @NonNull String jsonString
            , @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();
            try {

                Client baseClient = opdEventClient.getClient();
                Event baseEvent = opdEventClient.getEvent();

                if (baseClient != null && params.isEditMode()) {
                    try {
                        OpdJsonFormUtils.mergeAndSaveClient(baseClient);
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

            long lastSyncTimeStamp = OpdLibrary.getInstance().context().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            OpdLibrary.getInstance().getClientProcessorForJava().processClient(OpdLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            OpdLibrary.getInstance().context().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void addImageLocation(@NonNull String jsonString, @Nullable Client baseClient, @Nullable Event baseEvent) {
        if (baseClient != null || baseEvent != null) {
            String imageLocation = OpdJsonFormUtils.getFieldValue(jsonString, OpdConstants.KEY.PHOTO);
            if (StringUtils.isNotBlank(imageLocation)) {
                OpdJsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }

    private void updateOpenSRPId(@NonNull String jsonString, @NonNull RegisterParams params, @Nullable Client baseClient) {
        if (params.isEditMode() && baseClient != null) {
            // Unassign current OPENSRP ID
            try {
                String newOpenSRPId = baseClient.getIdentifier(OpdJsonFormUtils.OPENSRP_ID).replace("-", "");
                String currentOpenSRPId = OpdJsonFormUtils.getString(jsonString, OpdJsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                if (!newOpenSRPId.equals(currentOpenSRPId)) {
                    //OPENSRP ID was changed
                    OpdLibrary.getInstance().getUniqueIdRepository().open(currentOpenSRPId);
                }
            } catch (Exception e) {//might crash if M_ZEIR
                Timber.d(e);
            }
        }

    }

    @Nullable
    private String addEvent(RegisterParams params, Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(OpdJsonFormUtils.gson.toJson(baseEvent));
            OpdLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
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

package org.smartregister.maternity.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jeasy.rules.api.Facts;
import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.contract.MaternityProfileOverviewFragmentContract;
import org.smartregister.maternity.domain.YamlConfig;
import org.smartregister.maternity.domain.YamlConfigItem;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.model.MaternityProfileOverviewFragmentModel;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.pojos.OpdVisit;
import org.smartregister.maternity.utils.FilePath;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityFactsUtil;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.util.DateUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityProfileOverviewFragmentPresenter implements MaternityProfileOverviewFragmentContract.Presenter {

    private MaternityProfileOverviewFragmentModel model;
    private CommonPersonObjectClient client;
    private WeakReference<MaternityProfileOverviewFragmentContract.View> view;

    public MaternityProfileOverviewFragmentPresenter(@NonNull MaternityProfileOverviewFragmentContract.View view) {
        this.view = new WeakReference<>(view);
        model = new MaternityProfileOverviewFragmentModel();
    }

    @Override
    public void loadOverviewFacts(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        model.fetchLastCheckAndVisit(baseEntityId, new MaternityProfileOverviewFragmentContract.Model.OnFetchedCallback() {
            @Override
            public void onFetched(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable MaternityDetails maternityDetails) {
                loadOverviewDataAndDisplay(opdCheckIn, opdVisit, maternityDetails, onFinishedCallback);
            }
        });
    }

    @Override
    public void loadOverviewDataAndDisplay(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable MaternityDetails maternityDetails, @NonNull final OnFinishedCallback onFinishedCallback) {
        List<YamlConfigWrapper> yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
        Facts facts = new Facts();
        setDataFromCheckIn(opdCheckIn, opdVisit, maternityDetails, facts);

        try {
            generateYamlConfigList(facts, yamlConfigListGlobal);
        } catch (IOException ioException) {
            Timber.e(ioException);
        }

        onFinishedCallback.onFinished(facts, yamlConfigListGlobal);
    }

    private void generateYamlConfigList(@NonNull Facts facts, @NonNull List<YamlConfigWrapper> yamlConfigListGlobal) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.OPD_PROFILE_OVERVIEW);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;

            YamlConfig yamlConfig = (YamlConfig) ruleObject;
            if (yamlConfig.getGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(yamlConfig.getGroup(), null, null));
            }

            if (yamlConfig.getSubGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null));
            }

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            if (configItems != null) {

                for (YamlConfigItem configItem : configItems) {
                    String relevance = configItem.getRelevance();
                    if (relevance != null && MaternityLibrary.getInstance().getMaternityRulesEngineHelper()
                            .getRelevance(facts, relevance)) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }
            }

            if (valueCount > 0) {
                yamlConfigListGlobal.addAll(yamlConfigList);
            }
        }
    }

    @Override
    public void setDataFromCheckIn(@Nullable OpdCheckIn checkIn, @Nullable OpdVisit visit, @Nullable MaternityDetails maternityDetails, @NonNull Facts facts) {
        String unknownString = getString(R.string.unknown);
        if (checkIn != null) {
            if (client != null && AllConstants.FEMALE_GENDER.equalsIgnoreCase(client.getColumnmaps().get(MaternityConstants.ClientMapKey.GENDER))) {
                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.PREGNANCY_STATUS, checkIn.getPregnancyStatus());
            }

            String currentHivResult = checkIn.getCurrentHivResult() != null ? checkIn.getCurrentHivResult() : unknownString;


            MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.PATIENT_ON_ART, checkIn.getIsTakingArt());

            // Client is currently checked-in, show the current check-in details
            if (MaternityLibrary.getInstance().isClientCurrentlyCheckedIn(visit, maternityDetails)) {
                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.IS_PREVIOUSLY_TESTED_HIV, checkIn.getHasHivTestPreviously());
                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.PREVIOUSLY_HIV_STATUS_RESULTS, checkIn.getHivResultsPreviously());

                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.CURRENT_HIV_STATUS, currentHivResult);

                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.VISIT_TYPE, checkIn.getVisitType());
                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.APPOINTMENT_SCHEDULED_PREVIOUSLY, checkIn.getAppointmentScheduledPreviously());
                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.DATE_OF_APPOINTMENT, checkIn.getAppointmentDueDate());
            } else {
                MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.HIV_STATUS, currentHivResult);
            }
        } else {
            if (client != null && unknownString != null) {
                if (AllConstants.FEMALE_GENDER.equalsIgnoreCase(client.getColumnmaps().get(MaternityConstants.ClientMapKey.GENDER))) {
                    MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.PREGNANCY_STATUS, unknownString);
                } else {
                    MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.HIV_STATUS, unknownString);
                }
            }
        }

        boolean shouldCheckIn = MaternityLibrary.getInstance().canPatientCheckInInsteadOfDiagnoseAndTreat(visit, maternityDetails);
        facts.put(MaternityDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT, !shouldCheckIn);

        if (visit != null && visit.getVisitDate() != null && checkIn != null && checkIn.getAppointmentDueDate() != null) {
            facts.put(MaternityConstants.FactKey.VISIT_TO_APPOINTMENT_DATE, getVisitToAppointmentDateDuration(visit.getVisitDate(), checkIn.getAppointmentDueDate()));
        }
    }

    private Iterable<Object> loadFile(@NonNull String filename) throws IOException {
        return MaternityLibrary.getInstance().readYaml(filename);
    }

    @NonNull
    private String getVisitToAppointmentDateDuration(@NonNull Date visitDate, @NonNull String appointmentDueDateString) {
        Date appointmentDueDate = MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD, appointmentDueDateString);
        if (appointmentDueDate != null) {
            return DateUtil.getDuration(appointmentDueDate.getTime() - visitDate.getTime());
        }

        return "";
    }

    public void setClient(@NonNull CommonPersonObjectClient client) {
        this.client = client;
    }

    @Nullable
    @Override
    public MaternityProfileOverviewFragmentContract.View getProfileView() {
        MaternityProfileOverviewFragmentContract.View view = this.view.get();
        if (view != null) {
            return view;
        }

        return null;
    }

    @Nullable
    @Override
    public String getString(int stringId) {
        if (getProfileView() != null) {
            return getProfileView().getString(stringId);
        }

        return null;
    }


}

package org.smartregister.opd.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.interactor.OpdProfileVisitsFragmentInteractor;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.pojos.OpdVisitSummaryResultModel;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdFactsUtil;
import org.smartregister.opd.utils.OpdUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileVisitsFragmentPresenter implements OpdProfileVisitsFragmentContract.Presenter {

    private WeakReference<OpdProfileVisitsFragmentContract.View> mProfileView;
    private OpdProfileVisitsFragmentContract.Interactor mProfileInteractor;
    private int currentPageNo = 0;
    private int totalPages = 0;

    public OpdProfileVisitsFragmentPresenter(@NonNull OpdProfileVisitsFragmentContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new OpdProfileVisitsFragmentInteractor(this);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (! isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public void loadVisits(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisits(baseEntityId, currentPageNo, new OnVisitsLoadedCallback() {

                @Override
                public void onVisitsLoaded(@NonNull List<OpdVisitSummary> opdVisitSummaries) {
                    updatePageCounter();

                    ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
                    populateWrapperDataAndFacts(opdVisitSummaries, items);
                    onFinishedCallback.onFinished(opdVisitSummaries, items);
                }
            });

        }
    }

    @Override
    public void loadPageCounter(@NonNull String baseEntityId) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisitsPageCount(baseEntityId, new OpdProfileVisitsFragmentContract.Interactor.OnFetchVisitsPageCountCallback() {
                @Override
                public void onFetchVisitsPageCount(int visitsPageCount) {
                    totalPages = visitsPageCount;
                    updatePageCounter();
                }
            });
        }
    }

    private void updatePageCounter() {
        String pageCounterTemplate = getString(R.string.current_page_of_total_pages);

        OpdProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null && pageCounterTemplate != null) {
            profileView.showPageCountText(String.format(pageCounterTemplate, (currentPageNo + 1), totalPages));

            profileView.showPreviousPageBtn(currentPageNo > 0);
            profileView.showNextPageBtn(currentPageNo < (totalPages -1));
        }
    }

    @Override
    public void populateWrapperDataAndFacts(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        for (OpdVisitSummary opdVisitSummary: opdVisitSummaries) {
            Facts facts = generateOpdVisitSummaryFact(opdVisitSummary);
            Iterable<Object> ruleObjects = null;

            try {
                ruleObjects = OpdLibrary.getInstance().readYaml(FilePath.FILE.OPD_VISIT_ROW);
            } catch (IOException e) {
                Timber.e(e);
            }

            if (ruleObjects != null) {
                for (Object ruleObject : ruleObjects) {
                    YamlConfig yamlConfig = (YamlConfig) ruleObject;
                    if (yamlConfig.getGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(yamlConfig.getGroup(), null, null), facts));
                    }

                    if (yamlConfig.getSubGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null), facts));
                    }

                    List<YamlConfigItem> configItems = yamlConfig.getFields();

                    if (configItems != null) {
                        for (YamlConfigItem configItem : configItems) {
                            String relevance = configItem.getRelevance();
                            if (relevance != null && OpdLibrary.getInstance().getOpdRulesEngineHelper()
                                    .getRelevance(facts, relevance)) {
                                YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper(null, null, configItem);
                                items.add(new Pair<>(yamlConfigWrapper, facts));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onNextPageClicked() {
        if (currentPageNo < totalPages && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo++;

            loadVisits(getProfileView().getClientBaseEntityId(), new OnFinishedCallback() {
                @Override
                public void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                    if (getProfileView() != null) {
                        getProfileView().displayVisits(opdVisitSummaries, items);
                    }
                }
            });
        }
    }

    @Override
    public void onPreviousPageClicked() {
        if (currentPageNo > 0 && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo--;

            loadVisits(getProfileView().getClientBaseEntityId(), new OnFinishedCallback() {
                @Override
                public void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                    if (getProfileView() != null) {
                        getProfileView().displayVisits(opdVisitSummaries, items);
                    }
                }
            });
        }
    }

    @NonNull
    private Facts generateOpdVisitSummaryFact(@NonNull OpdVisitSummary opdVisitSummary) {
        Facts facts = new Facts();

        if (opdVisitSummary.getVisitDate() != null) {
            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.VISIT_DATE, OpdUtils.convertDate(opdVisitSummary.getVisitDate(), OpdConstants.DateFormat.d_MMM_yyyy));
        }

        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TEST_NAME, opdVisitSummary.getTestName());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TEST_RESULT, opdVisitSummary.getTestResult());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS, opdVisitSummary.getDiagnosis());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS_TYPE, opdVisitSummary.getDiagnosisType());

        // Put the diseases text
        String diseasesText = generateDiseasesText(opdVisitSummary);
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DISEASE_CODE, diseasesText);

        // Put the treatment text
        HashMap<String, OpdVisitSummaryResultModel.Treatment> treatments = opdVisitSummary.getTreatments();
        String medicationText = generateMedicationText(treatments);
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TREATMENT, medicationText);

        // Add translate-able labels
        setLabelsInFacts(facts);

        return facts;
    }

    @Override
    @NonNull
    public String generateMedicationText(@NonNull HashMap<String, OpdVisitSummaryResultModel.Treatment> treatments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (OpdVisitSummaryResultModel.Treatment treatment : treatments.values()) {
            if (treatment != null && treatment.getMedicine() != null) {
                String medicine = treatment.getMedicine();
                if (stringBuilder.length() > 1) {
                    stringBuilder.append("<br/>");
                }

                String medicationTemplate = getString(R.string.single_medicine_visit_preview_summary);
                String doseOrDurationHtml = getString(R.string.dose_or_duration_html);

                if (medicationTemplate != null && !TextUtils.isEmpty(medicine)) {
                    stringBuilder.append(String.format(medicationTemplate
                            , medicine));

                    StringBuilder doseAndDurationText = new StringBuilder();
                    String dosage = treatment.getDosage();
                    if (!TextUtils.isEmpty(dosage)) {
                        String medicationDoseTemplate = getString(R.string.medication_dose);
                        if (medicationDoseTemplate != null) {
                            doseAndDurationText.append(String.format(medicationDoseTemplate, dosage));

                            if (!TextUtils.isEmpty(treatment.getDuration())) {
                                doseAndDurationText.append(". ");
                            }
                        }
                    }

                    String duration = treatment.getDuration();
                    if (!TextUtils.isEmpty(duration)) {
                        String medicationDurationTemplate = getString(R.string.medication_duration);
                        if (medicationDurationTemplate != null) {
                            doseAndDurationText.append(String.format(medicationDurationTemplate, duration));
                        }
                    }

                    if (doseAndDurationText.length() > 0 && doseOrDurationHtml != null) {
                        stringBuilder.append(String.format(doseOrDurationHtml, doseAndDurationText.toString()));
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    @NonNull
    public String generateDiseasesText(@NonNull OpdVisitSummary opdVisitSummary) {
        HashSet<String> diseases = opdVisitSummary.getDiseases();
        Iterator<String> diseaseIterator = diseases.iterator();

        StringBuilder stringBuilder = new StringBuilder();

        while (diseaseIterator.hasNext()) {
            String disease = diseaseIterator.next();
            if (!TextUtils.isEmpty(disease)) {
                stringBuilder.append(disease);

                if (diseaseIterator.hasNext()) {
                    stringBuilder.append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    private void setLabelsInFacts(@NonNull Facts facts) {
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS_LABEL, getString(R.string.diagnosis));
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS_TYPE_LABEL, getString(R.string.diagnosis_type));
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DISEASE_CODE_LABEL, getString(R.string.disease_code));
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TREATMENT_LABEL, getString(R.string.treatment));
    }

    @Nullable
    @Override
    public OpdProfileVisitsFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Nullable
    public String getString(@StringRes int stringId) {
        OpdProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null) {
            return profileView.getString(stringId);
        }

        return null;
    }
}
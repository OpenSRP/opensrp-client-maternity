package org.smartregister.maternity.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.maternity.R;
import org.smartregister.maternity.contract.MaternityProfileVisitsFragmentContract;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.interactor.MaternityProfileVisitsFragmentInteractor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class MaternityProfileVisitsFragmentPresenter implements MaternityProfileVisitsFragmentContract.Presenter {

    private WeakReference<MaternityProfileVisitsFragmentContract.View> mProfileView;
    private MaternityProfileVisitsFragmentContract.Interactor mProfileInteractor;
    private int currentPageNo = 0;
    private int totalPages = 0;

    public MaternityProfileVisitsFragmentPresenter(@NonNull MaternityProfileVisitsFragmentContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new MaternityProfileVisitsFragmentInteractor(this);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public void loadVisits(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisits(baseEntityId, currentPageNo, new OnVisitsLoadedCallback() {

                @Override
                public void onVisitsLoaded(@NonNull List<Object> ancVisitSummaries) {
                    updatePageCounter();

                    ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
                    populateWrapperDataAndFacts(ancVisitSummaries, items);
                    onFinishedCallback.onFinished(ancVisitSummaries, items);
                }
            });

        }
    }

    @Override
    public void loadPageCounter(@NonNull String baseEntityId) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisitsPageCount(baseEntityId, new MaternityProfileVisitsFragmentContract.Interactor.OnFetchVisitsPageCountCallback() {
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

        MaternityProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null && pageCounterTemplate != null) {
            profileView.showPageCountText(String.format(pageCounterTemplate, (currentPageNo + 1), totalPages));

            profileView.showPreviousPageBtn(currentPageNo > 0);
            profileView.showNextPageBtn(currentPageNo < (totalPages - 1));
        }
    }

    @Override
    public void populateWrapperDataAndFacts(@NonNull List<Object> ancVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        /*for (OpdVisitSummary opdVisitSummary: opdVisitSummaries) {
            Facts facts = generateOpdVisitSummaryFact(opdVisitSummary);
            Iterable<Object> ruleObjects = null;

            try {
                ruleObjects = MaternityLibrary.getInstance().readYaml(FilePath.FILE.MATERNITY_VISIT_ROW);
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
                            if (relevance != null && MaternityLibrary.getInstance().getMaternityRulesEngineHelper()
                                    .getRelevance(facts, relevance)) {
                                YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper(null, null, configItem);
                                items.add(new Pair<>(yamlConfigWrapper, facts));
                            }
                        }
                    }
                }
            }
        }*/
    }

    @Override
    public void onNextPageClicked() {
        if (currentPageNo < totalPages && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo++;

            loadVisits(getProfileView().getClientBaseEntityId(), new OnFinishedCallback() {
                @Override
                public void onFinished(@NonNull List<Object> ancVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                    if (getProfileView() != null) {
                        getProfileView().displayVisits(ancVisitSummaries, items);
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
                public void onFinished(@NonNull List<Object> ancVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                    if (getProfileView() != null) {
                        getProfileView().displayVisits(ancVisitSummaries, items);
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public MaternityProfileVisitsFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Nullable
    public String getString(@StringRes int stringId) {
        MaternityProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null) {
            return profileView.getString(stringId);
        }

        return null;
    }
}
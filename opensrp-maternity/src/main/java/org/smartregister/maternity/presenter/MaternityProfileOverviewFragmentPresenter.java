package org.smartregister.maternity.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.contract.MaternityProfileOverviewFragmentContract;
import org.smartregister.maternity.domain.YamlConfig;
import org.smartregister.maternity.domain.YamlConfigItem;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.model.MaternityProfileOverviewFragmentModel;
import org.smartregister.maternity.utils.FilePath;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityFactsUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityProfileOverviewFragmentPresenter implements MaternityProfileOverviewFragmentContract.Presenter {

    private MaternityProfileOverviewFragmentModel model;
    private WeakReference<MaternityProfileOverviewFragmentContract.View> view;

    public MaternityProfileOverviewFragmentPresenter(@NonNull MaternityProfileOverviewFragmentContract.View view) {
        this.view = new WeakReference<>(view);
        model = new MaternityProfileOverviewFragmentModel();
    }

    @Override
    public void loadOverviewFacts(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        model.fetchMaternityOverviewDetails(baseEntityId, maternityDetails -> {
            loadOverviewDataAndDisplay(maternityDetails, onFinishedCallback);

            // Update the client map
            CommonPersonObjectClient commonPersonObjectClient = getProfileView().getActivityClientMap();
            if (commonPersonObjectClient != null) {
                commonPersonObjectClient.getColumnmaps().putAll(maternityDetails);
                commonPersonObjectClient.getDetails().putAll(maternityDetails);
            }
        });
    }

    @Override
    public void loadOverviewDataAndDisplay(@NonNull HashMap<String, String> maternityDetails, @NonNull final OnFinishedCallback onFinishedCallback) {
        List<YamlConfigWrapper> yamlConfigListGlobal = new ArrayList<>();
        Facts facts = new Facts();
        setDataFromRegistration(maternityDetails, facts);

        try {
            generateYamlConfigList(facts, yamlConfigListGlobal);
        } catch (IOException ioException) {
            Timber.e(ioException);
        }

        onFinishedCallback.onFinished(facts, yamlConfigListGlobal);
    }

    private void generateYamlConfigList(@NonNull Facts facts, @NonNull List<YamlConfigWrapper> yamlConfigListGlobal) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.MATERNITY_PROFILE_OVERVIEW);

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
    public void setDataFromRegistration(@NonNull HashMap<String, String> maternityDetails, @NonNull Facts facts) {
        for (Map.Entry<String, String> entry : maternityDetails.entrySet()) {
            String value = entry.getValue();
            MaternityFactsUtil.putNonNullFact(facts, entry.getKey(), StringUtils.isBlank(value) ? "" : value.replaceAll("\"", ""));
        }

        String currentHivStatus = maternityDetails.get("hiv_status_current");
        String hivStatus = currentHivStatus == null ? getString(R.string.unknown) : currentHivStatus;
        MaternityFactsUtil.putNonNullFact(facts, MaternityConstants.FactKey.ProfileOverview.HIV_STATUS, hivStatus);
    }

    private Iterable<Object> loadFile(@NonNull String filename) throws IOException {
        return MaternityLibrary.getInstance().readYaml(filename);
    }

    public void setClient(@NonNull CommonPersonObjectClient client) {
        // Do nothing
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

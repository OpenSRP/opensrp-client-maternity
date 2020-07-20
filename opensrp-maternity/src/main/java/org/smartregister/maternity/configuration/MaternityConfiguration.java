package org.smartregister.maternity.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.HashMap;

/**
 * This is the object used to configure any configurations added to Maternity. We mostly use objects that are
 * instantiated using {@link org.smartregister.maternity.utils.ConfigurationInstancesHelper} which means
 * that the constructors of any of the classes should not have any parameters
 * <p>
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityConfiguration {

    private Builder builder;

    private MaternityConfiguration(@NonNull Builder builder) {
        this.builder = builder;
        setDefaults();
    }

    private void setDefaults() {
        if (builder.maternityRegisterProviderMetadata == null) {
            builder.maternityRegisterProviderMetadata = BaseMaternityRegisterProviderMetadata.class;
        }

        if (!builder.maternityFormProcessingClasses.containsKey(MaternityConstants.EventType.MATERNITY_OUTCOME)) {
            builder.maternityFormProcessingClasses.put(MaternityConstants.EventType.MATERNITY_OUTCOME, MaternityOutcomeFormProcessingTask.class);
        }

        if (!builder.maternityFormProcessingClasses.containsKey(MaternityConstants.EventType.MATERNITY_MEDIC_INFO)) {
            builder.maternityFormProcessingClasses.put(MaternityConstants.EventType.MATERNITY_MEDIC_INFO, MaternityMedicInfoFormProcessingTask.class);
        }
    }

    @Nullable
    public MaternityMetadata getMaternityMetadata() {
        return builder.maternityMetadata;
    }

    @NonNull
    public Class<? extends MaternityRegisterProviderMetadata> getMaternityRegisterProviderMetadata() {
        return builder.maternityRegisterProviderMetadata;
    }

    @Nullable
    public Class<? extends MaternityRegisterRowOptions> getMaternityRegisterRowOptions() {
        return builder.maternityRegisterRowOptions;
    }

    @NonNull
    public Class<? extends MaternityRegisterQueryProviderContract> getMaternityRegisterQueryProvider() {
        return builder.maternityRegisterQueryProvider;
    }

    @Nullable
    public Class<? extends MaternityRegisterSwitcher> getMaternityRegisterSwitcher() {
        return builder.maternityRegisterSwitcher;
    }

    public Class<? extends MaternityFormProcessingTask> getMaternityFormProcessingTasks(@NonNull String eventType) {
        return builder.maternityFormProcessingClasses.get(eventType);
    }

    public int getMaxCheckInDurationInMinutes() {
        return builder.maxCheckInDurationInMinutes;
    }

    public boolean isBottomNavigationEnabled() {
        return builder.isBottomNavigationEnabled;
    }

    public static class Builder {

        @Nullable
        private Class<? extends MaternityRegisterProviderMetadata> maternityRegisterProviderMetadata;

        @Nullable
        private Class<? extends MaternityRegisterRowOptions> maternityRegisterRowOptions;

        @NonNull
        private Class<? extends MaternityRegisterQueryProviderContract> maternityRegisterQueryProvider;

        @Nullable
        private Class<? extends MaternityRegisterSwitcher> maternityRegisterSwitcher;

        @NonNull
        private HashMap<String, Class<? extends MaternityFormProcessingTask>> maternityFormProcessingClasses = new HashMap<>();

        private boolean isBottomNavigationEnabled;

        private MaternityMetadata maternityMetadata;

        private int maxCheckInDurationInMinutes = 24 * 60;

        public Builder(@NonNull Class<? extends MaternityRegisterQueryProviderContract> maternityRegisterQueryProvider) {
            this.maternityRegisterQueryProvider = maternityRegisterQueryProvider;
        }

        public Builder setMaternityRegisterProviderMetadata(@Nullable Class<? extends MaternityRegisterProviderMetadata> maternityRegisterProviderMetadata) {
            this.maternityRegisterProviderMetadata = maternityRegisterProviderMetadata;
            return this;
        }

        public Builder setMaternityRegisterRowOptions(@Nullable Class<? extends MaternityRegisterRowOptions> maternityRegisterRowOptions) {
            this.maternityRegisterRowOptions = maternityRegisterRowOptions;
            return this;
        }

        public Builder setMaternityRegisterSwitcher(@Nullable Class<? extends MaternityRegisterSwitcher> maternityRegisterSwitcher) {
            this.maternityRegisterSwitcher = maternityRegisterSwitcher;
            return this;
        }

        public Builder setBottomNavigationEnabled(boolean isBottomNavigationEnabled) {
            this.isBottomNavigationEnabled = isBottomNavigationEnabled;
            return this;
        }

        public Builder setMaternityMetadata(@NonNull MaternityMetadata maternityMetadata) {
            this.maternityMetadata = maternityMetadata;
            return this;
        }

        public Builder setMaxCheckInDurationInMinutes(int durationInMinutes) {
            this.maxCheckInDurationInMinutes = durationInMinutes;
            return this;
        }

        public Builder addMaternityFormProcessingTask(@NonNull String eventType, @NonNull Class<? extends MaternityFormProcessingTask> maternityFormProcessingTask) {
            this.maternityFormProcessingClasses.put(eventType, maternityFormProcessingTask);
            return this;
        }

        public MaternityConfiguration build() {
            return new MaternityConfiguration(this);
        }

    }

}

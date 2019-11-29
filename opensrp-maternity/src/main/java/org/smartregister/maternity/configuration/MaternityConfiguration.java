package org.smartregister.maternity.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.maternity.pojos.MaternityMetadata;

/**
 * This is the object used to configure any configurations added to OPD. We mostly use objects that are
 * instantiated using {@link org.smartregister.maternity.utils.ConfigurationInstancesHelper} which means
 * that the constructors of any of the classes should not have any parameters
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityConfiguration {

    private Builder builder;

    private MaternityConfiguration(@NonNull Builder builder) {
        this.builder = builder;

        setDefaults();
    }

    private void setDefaults() {
        if (builder.opdRegisterProviderMetadata == null) {
            builder.opdRegisterProviderMetadata = BaseMaternityRegisterProviderMetadata.class;
        }
    }

    @Nullable
    public MaternityMetadata getOpdMetadata() {
        return builder.maternityMetadata;
    }

    @NonNull
    public Class<? extends MaternityRegisterProviderMetadata> getOpdRegisterProviderMetadata() {
        return builder.opdRegisterProviderMetadata;
    }

    @Nullable
    public Class<? extends MaternityRegisterRowOptions> getOpdRegisterRowOptions() {
        return builder.opdRegisterRowOptions;
    }

    @NonNull
    public Class<? extends MaternityRegisterQueryProviderContract> getOpdRegisterQueryProvider() {
        return builder.opdRegisterQueryProvider;
    }

    @Nullable
    public Class<? extends MaternityRegisterSwitcher> getOpdRegisterSwitcher() {
        return builder.opdRegisterSwitcher;
    }

    public int getMaxCheckInDurationInMinutes() {
        return builder.maxCheckInDurationInMinutes;
    }

    public boolean isBottomNavigationEnabled() {
        return builder.isBottomNavigationEnabled;
    }

    public static class Builder {

        @Nullable
        private Class<? extends MaternityRegisterProviderMetadata> opdRegisterProviderMetadata;

        @Nullable
        private Class<? extends MaternityRegisterRowOptions> opdRegisterRowOptions;

        @NonNull
        private Class<? extends MaternityRegisterQueryProviderContract> opdRegisterQueryProvider;

        @Nullable
        private Class<? extends MaternityRegisterSwitcher> opdRegisterSwitcher;

        private boolean isBottomNavigationEnabled;

        private MaternityMetadata maternityMetadata;
        private int maxCheckInDurationInMinutes = 24 * 60;

        public Builder(@NonNull Class<? extends MaternityRegisterQueryProviderContract> opdRegisterQueryProvider) {
            this.opdRegisterQueryProvider = opdRegisterQueryProvider;
        }

        public Builder setOpdRegisterProviderMetadata(@Nullable Class<? extends MaternityRegisterProviderMetadata> opdRegisterProviderMetadata) {
            this.opdRegisterProviderMetadata = opdRegisterProviderMetadata;
            return this;
        }

        public Builder setOpdRegisterRowOptions(@Nullable Class<? extends MaternityRegisterRowOptions> opdRegisterRowOptions) {
            this.opdRegisterRowOptions = opdRegisterRowOptions;
            return this;
        }

        public Builder setOpdRegisterSwitcher(@Nullable Class<? extends MaternityRegisterSwitcher> opdRegisterSwitcher) {
            this.opdRegisterSwitcher = opdRegisterSwitcher;
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

        public MaternityConfiguration build() {
            return new MaternityConfiguration(this);
        }

    }

}

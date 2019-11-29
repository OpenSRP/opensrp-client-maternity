package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.OpdMetadata;

/**
 * This is the object used to configure any configurations added to OPD. We mostly use objects that are
 * instantiated using {@link org.smartregister.opd.utils.ConfigurationInstancesHelper} which means
 * that the constructors of any of the classes should not have any parameters
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdConfiguration {

    private Builder builder;

    private OpdConfiguration(@NonNull Builder builder) {
        this.builder = builder;

        setDefaults();
    }

    private void setDefaults() {
        if (builder.opdRegisterProviderMetadata == null) {
            builder.opdRegisterProviderMetadata = BaseOpdRegisterProviderMetadata.class;
        }
    }

    @Nullable
    public OpdMetadata getOpdMetadata() {
        return builder.opdMetadata;
    }

    @NonNull
    public Class<? extends OpdRegisterProviderMetadata> getOpdRegisterProviderMetadata() {
        return builder.opdRegisterProviderMetadata;
    }

    @Nullable
    public Class<? extends OpdRegisterRowOptions> getOpdRegisterRowOptions() {
        return builder.opdRegisterRowOptions;
    }

    @NonNull
    public Class<? extends OpdRegisterQueryProviderContract> getOpdRegisterQueryProvider() {
        return builder.opdRegisterQueryProvider;
    }

    @Nullable
    public Class<? extends OpdRegisterSwitcher> getOpdRegisterSwitcher() {
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
        private Class<? extends OpdRegisterProviderMetadata> opdRegisterProviderMetadata;

        @Nullable
        private Class<? extends OpdRegisterRowOptions> opdRegisterRowOptions;

        @NonNull
        private Class<? extends OpdRegisterQueryProviderContract> opdRegisterQueryProvider;

        @Nullable
        private Class<? extends OpdRegisterSwitcher> opdRegisterSwitcher;

        private boolean isBottomNavigationEnabled;

        private OpdMetadata opdMetadata;
        private int maxCheckInDurationInMinutes = 24 * 60;

        public Builder(@NonNull Class<? extends OpdRegisterQueryProviderContract> opdRegisterQueryProvider) {
            this.opdRegisterQueryProvider = opdRegisterQueryProvider;
        }

        public Builder setOpdRegisterProviderMetadata(@Nullable Class<? extends OpdRegisterProviderMetadata> opdRegisterProviderMetadata) {
            this.opdRegisterProviderMetadata = opdRegisterProviderMetadata;
            return this;
        }

        public Builder setOpdRegisterRowOptions(@Nullable Class<? extends OpdRegisterRowOptions> opdRegisterRowOptions) {
            this.opdRegisterRowOptions = opdRegisterRowOptions;
            return this;
        }

        public Builder setOpdRegisterSwitcher(@Nullable Class<? extends OpdRegisterSwitcher> opdRegisterSwitcher) {
            this.opdRegisterSwitcher = opdRegisterSwitcher;
            return this;
        }

        public Builder setBottomNavigationEnabled(boolean isBottomNavigationEnabled) {
            this.isBottomNavigationEnabled = isBottomNavigationEnabled;
            return this;
        }

        public Builder setOpdMetadata(@NonNull OpdMetadata opdMetadata) {
            this.opdMetadata = opdMetadata;
            return this;
        }

        public Builder setMaxCheckInDurationInMinutes(int durationInMinutes) {
            this.maxCheckInDurationInMinutes = durationInMinutes;
            return this;
        }

        public OpdConfiguration build() {
            return new OpdConfiguration(this);
        }

    }

}

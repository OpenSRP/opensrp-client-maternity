package org.smartregister.opd.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.configuration.OpdRegisterProviderMetadata;
import org.smartregister.opd.configuration.OpdRegisterRowOptions;
import org.smartregister.opd.holders.FooterViewHolder;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.ConfigurationInstancesHelper;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.opd.utils.OpdViewConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterProvider implements RecyclerViewProvider<OpdRegisterViewHolder> {
    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;

    private OpdRegisterProviderMetadata opdRegisterProviderMetadata;

    @Nullable
    private OpdRegisterRowOptions opdRegisterRowOptions;

    public OpdRegisterProvider(@NonNull Context context, @NonNull View.OnClickListener onClickListener, @NonNull View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;

        // Get the configuration
        this.opdRegisterProviderMetadata = ConfigurationInstancesHelper
                .newInstance(OpdLibrary.getInstance()
                        .getOpdConfiguration()
                        .getOpdRegisterProviderMetadata());

        Class<? extends OpdRegisterRowOptions> opdRegisterRowOptionsClass = OpdLibrary.getInstance()
                .getOpdConfiguration()
                .getOpdRegisterRowOptions();
        if (opdRegisterRowOptionsClass != null) {
            this.opdRegisterRowOptions = ConfigurationInstancesHelper.newInstance(opdRegisterRowOptionsClass);
        }
    }

    public static void fillValue(@Nullable TextView v, @NonNull String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (opdRegisterRowOptions != null && opdRegisterRowOptions.isDefaultPopulatePatientColumn()) {
            opdRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
        } else {
            populatePatientColumn(pc, viewHolder);

            if (opdRegisterRowOptions != null) {
                opdRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
            }
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public OpdRegisterViewHolder createViewHolder(ViewGroup parent) {
        int resId = R.layout.opd_register_list_row;

        if (opdRegisterRowOptions != null
                && opdRegisterRowOptions.useCustomViewLayout()
                && opdRegisterRowOptions.getCustomViewLayoutId() != 0) {
            resId = opdRegisterRowOptions.getCustomViewLayoutId();
        }

        View view = inflater.inflate(resId, parent, false);

        if (opdRegisterRowOptions != null && opdRegisterRowOptions.isCustomViewHolder()) {
            return opdRegisterRowOptions.createCustomViewHolder(view);
        } else {
            return new OpdRegisterViewHolder(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    public void populatePatientColumn(CommonPersonObjectClient commonPersonObjectClient, OpdRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = commonPersonObjectClient.getColumnmaps();

        if (opdRegisterProviderMetadata.isClientHaveGuardianDetails(patientColumnMaps)) {
            viewHolder.showCareGiverName();

            String parentFirstName = opdRegisterProviderMetadata.getGuardianFirstName(patientColumnMaps);
            String parentLastName = opdRegisterProviderMetadata.getGuardianLastName(patientColumnMaps);
            String parentMiddleName = opdRegisterProviderMetadata.getGuardianMiddleName(patientColumnMaps);

            String parentName = context.getResources().getString(R.string.care_giver_initials)
                    + ": "
                    + org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName);
            fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
        } else {
            viewHolder.removeCareGiverName();
        }

        String firstName = opdRegisterProviderMetadata.getClientFirstName(patientColumnMaps);
        String middleName = opdRegisterProviderMetadata.getClientMiddleName(patientColumnMaps);
        String lastName = opdRegisterProviderMetadata.getClientLastName(patientColumnMaps);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);

        String dobString = Utils.getDuration(opdRegisterProviderMetadata.getDob(patientColumnMaps));
        String translatedYearInitial = context.getResources().getString(R.string.abbrv_years);
        fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " +
                WordUtils.capitalize(OpdUtils.getClientAge(dobString, translatedYearInitial)));
        String registerType = opdRegisterProviderMetadata.getRegisterType(patientColumnMaps);

        if (!TextUtils.isEmpty(registerType)) {
            viewHolder.showRegisterType();
            fillValue(viewHolder.tvRegisterType, registerType);
        } else {
            viewHolder.hideRegisterType();
        }

        setAddressAndGender(commonPersonObjectClient, viewHolder);
        addButtonClickListeners(commonPersonObjectClient, viewHolder);
    }

    public void setAddressAndGender(CommonPersonObjectClient pc, OpdRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = pc.getColumnmaps();
        String address = opdRegisterProviderMetadata.getHomeAddress(patientColumnMaps);
        String gender = opdRegisterProviderMetadata.getGender(patientColumnMaps);

        fillValue(viewHolder.textViewGender, gender);

        if (TextUtils.isEmpty(address)) {
            viewHolder.removePersonLocation();
        } else {
            viewHolder.showPersonLocation();
            fillValue(viewHolder.tvLocation, address);
        }
    }

    public void addButtonClickListeners(@NonNull CommonPersonObjectClient client, OpdRegisterViewHolder viewHolder) {
        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(OpdViewConstants.Provider.CHILD_COLUMN, patient, client);
        attachPatientOnclickListener(OpdViewConstants.Provider.ACTION_BUTTON_COLUMN, viewHolder.dueButton, client);
    }

    public void attachPatientOnclickListener(@NonNull String viewType, @NonNull View view, @NonNull CommonPersonObjectClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(R.id.VIEW_TYPE, viewType);
        view.setTag(R.id.VIEW_CLIENT, client);
    }
}
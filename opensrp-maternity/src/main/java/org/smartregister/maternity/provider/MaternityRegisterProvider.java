package org.smartregister.maternity.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.R;
import org.smartregister.maternity.configuration.MaternityRegisterProviderMetadata;
import org.smartregister.maternity.configuration.MaternityRegisterRowOptions;
import org.smartregister.maternity.holders.FooterViewHolder;
import org.smartregister.maternity.holders.MaternityRegisterViewHolder;
import org.smartregister.maternity.utils.ConfigurationInstancesHelper;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.maternity.utils.MaternityViewConstants;
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
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterProvider implements RecyclerViewProvider<MaternityRegisterViewHolder> {
    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;

    private MaternityRegisterProviderMetadata maternityRegisterProviderMetadata;

    @Nullable
    private MaternityRegisterRowOptions maternityRegisterRowOptions;

    public MaternityRegisterProvider(@NonNull Context context, @NonNull View.OnClickListener onClickListener, @NonNull View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;

        // Get the configuration
        this.maternityRegisterProviderMetadata = ConfigurationInstancesHelper
                .newInstance(MaternityLibrary.getInstance()
                        .getMaternityConfiguration()
                        .getMaternityRegisterProviderMetadata());

        Class<? extends MaternityRegisterRowOptions> maternityRegisterRowOptionsClass = MaternityLibrary.getInstance()
                .getMaternityConfiguration()
                .getMaternityRegisterRowOptions();
        if (maternityRegisterRowOptionsClass != null) {
            this.maternityRegisterRowOptions = ConfigurationInstancesHelper.newInstance(maternityRegisterRowOptionsClass);
        }
    }

    public static void fillValue(@Nullable TextView v, @NonNull String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, MaternityRegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (maternityRegisterRowOptions != null && maternityRegisterRowOptions.isDefaultPopulatePatientColumn()) {
            maternityRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
        } else {
            populatePatientColumn(pc, viewHolder);

            if (maternityRegisterRowOptions != null) {
                maternityRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
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
    public MaternityRegisterViewHolder createViewHolder(ViewGroup parent) {
        int resId = R.layout.maternity_register_list_row;

        if (maternityRegisterRowOptions != null
                && maternityRegisterRowOptions.useCustomViewLayout()
                && maternityRegisterRowOptions.getCustomViewLayoutId() != 0) {
            resId = maternityRegisterRowOptions.getCustomViewLayoutId();
        }

        View view = inflater.inflate(resId, parent, false);

        if (maternityRegisterRowOptions != null && maternityRegisterRowOptions.isCustomViewHolder()) {
            return maternityRegisterRowOptions.createCustomViewHolder(view);
        } else {
            return new MaternityRegisterViewHolder(view);
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

    public void populatePatientColumn(CommonPersonObjectClient commonPersonObjectClient, MaternityRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = commonPersonObjectClient.getColumnmaps();

        String firstName = maternityRegisterProviderMetadata.getClientFirstName(patientColumnMaps);
        String middleName = maternityRegisterProviderMetadata.getClientMiddleName(patientColumnMaps);
        String lastName = maternityRegisterProviderMetadata.getClientLastName(patientColumnMaps);
        String patientName = Utils.getName(firstName, middleName + " " + lastName);

        String dobString = Utils.getDuration(maternityRegisterProviderMetadata.getDob(patientColumnMaps));
        String translatedYearInitial = context.getResources().getString(R.string.abbrv_years);
        fillValue(viewHolder.textViewPatientName, WordUtils.capitalize(patientName));

        fillValue(viewHolder.tvAge, String.format(context.getString(R.string.patient_age_holder), WordUtils.capitalize(MaternityUtils.getClientAge(dobString, translatedYearInitial))));
        String ga = maternityRegisterProviderMetadata.getGA(patientColumnMaps);
        fillValue(viewHolder.textViewGa, String.format(context.getString(R.string.patient_ga_holder), ga));

        String patientId = maternityRegisterProviderMetadata.getPatientID(patientColumnMaps);
        fillValue(viewHolder.tvPatientId, String.format(context.getString(R.string.patient_id_holder), patientId));

        addButtonClickListeners(commonPersonObjectClient, viewHolder);
    }

    public void addButtonClickListeners(@NonNull CommonPersonObjectClient client, MaternityRegisterViewHolder viewHolder) {

        MaternityUtils.setActionButtonStatus(viewHolder.dueButton, client.getCaseId());

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(MaternityViewConstants.Provider.PATIENT_COLUMN, patient, client);
        attachPatientOnclickListener(MaternityViewConstants.Provider.ACTION_BUTTON_COLUMN, viewHolder.dueButton, client);
    }

    public void attachPatientOnclickListener(@NonNull String viewType, @NonNull View view, @NonNull CommonPersonObjectClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(R.id.VIEW_TYPE, viewType);
        view.setTag(R.id.VIEW_CLIENT, client);
    }
}
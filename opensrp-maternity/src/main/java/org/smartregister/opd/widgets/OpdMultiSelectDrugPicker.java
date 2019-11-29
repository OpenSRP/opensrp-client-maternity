package org.smartregister.opd.widgets;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.utils.MultiSelectListUtils;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.domain.Setting;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.utils.OpdConstants;

import java.util.List;

import timber.log.Timber;

public class OpdMultiSelectDrugPicker extends MultiSelectListFactory implements TextWatcher {

    private Button opd_btn_save_drug;

    @Override
    protected void handleClickEventOnListData(@NonNull MultiSelectItem multiSelectItem) {
        createAdditionalDetailsDialog(multiSelectItem);
    }

    private void createAdditionalDetailsDialog(@NonNull final MultiSelectItem multiSelectItem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dosage_and_duration_layout, null);
        ImageView imgClose = view.findViewById(R.id.multiSelectListCloseDialog);
        opd_btn_save_drug = view.findViewById(R.id.opd_btn_save_drug);

        TextView txtSelectedItemInMultiSelectList = view.findViewById(R.id.txtSelectedItemInMultiSelectList);
        txtSelectedItemInMultiSelectList.setText(multiSelectItem.getText());
        final EditText edtTreatmentDuration = view.findViewById(R.id.edtTreatmentDuration);
        final EditText edtTreatmentDosage = view.findViewById(R.id.edtTreatmentDosage);
        edtTreatmentDosage.addTextChangedListener(this);
        edtTreatmentDuration.addTextChangedListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialogStyle);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

        opd_btn_save_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editableTreatmentDuration = edtTreatmentDuration.getText();
                Editable editableTreatmentDosage = edtTreatmentDosage.getText();

                String duration = "";
                if (editableTreatmentDuration != null) {
                    duration = editableTreatmentDuration.toString();
                }

                String dosage = "";
                if (editableTreatmentDuration != null) {
                    dosage = editableTreatmentDosage.toString();
                }

                writeAdditionalDetails(duration, dosage, multiSelectItem);

                writeToForm();

                alertDialog.dismiss();

                getAlertDialog().dismiss();
            }
        });
    }

    private void writeAdditionalDetails(@NonNull String duration, @NonNull String dosage, @NonNull MultiSelectItem multiSelectItem) {
        String multiSelectValue = multiSelectItem.getValue();
        String msg = String.format(context.getString(R.string.opd_dosage_duration_text), dosage, duration);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = new JSONObject(multiSelectValue);
            JSONObject jsonAdditionalObject = new JSONObject();
            jsonAdditionalObject.put(OpdConstants.JSON_FORM_KEY.DURATION, duration);
            jsonAdditionalObject.put(OpdConstants.JSON_FORM_KEY.DOSAGE, dosage);
            jsonAdditionalObject.put(JsonFormConstants.MultiSelectUtils.INFO, msg);
            jsonObject.put(JsonFormConstants.MultiSelectUtils.META, jsonAdditionalObject);
        } catch (JSONException e) {
            Timber.e(e);
        }

        multiSelectItem.setValue(jsonObject.toString());
        updateSelectedData(multiSelectItem, false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().isEmpty()) {
            if (opd_btn_save_drug != null) {
                opd_btn_save_drug.setTextColor(context.getResources().getColor(R.color.primary_text));
            }
        } else {
            if (opd_btn_save_drug != null) {
                opd_btn_save_drug.setTextColor(context.getResources().getColor(R.color.light_grey));
            }
        }
    }

    @Override
    public List<MultiSelectItem> fetchData() {
        Setting setting = OpdLibrary.getInstance().context().allSettings().getSetting(OpdConstants.SettingsConfig.OPD_MEDICINE);
        try {
            JSONObject jsonValObject = setting != null ? new JSONObject(setting.getValue()) : null;
            if (jsonValObject != null) {
                JSONArray jsonOptionsArray = jsonValObject.optJSONArray(AllConstants.SETTINGS);
                if (jsonOptionsArray != null) {
                    JSONArray jsonValuesArray = jsonOptionsArray.optJSONObject(0)
                            .optJSONArray(JsonFormConstants.VALUES);
                    if (jsonValuesArray != null) {
                        return MultiSelectListUtils.processOptionsJsonArray(jsonValuesArray);
                    }
                }
            }
            return null;
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
    }
}

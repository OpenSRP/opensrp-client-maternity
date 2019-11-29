package org.smartregister.maternity.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.widgets.MaternityBarcodeFactory;
import org.smartregister.maternity.widgets.MaternityEditTextFactory;
import org.smartregister.maternity.widgets.MaternityMultiSelectDrugPicker;
import org.smartregister.maternity.widgets.MaternityMultiSelectList;

public class MaternityFormInteractor extends JsonFormInteractor {

    private static final MaternityFormInteractor INSTANCE = new MaternityFormInteractor();

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new MaternityEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new MaternityBarcodeFactory());
        map.put(MaternityConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, new MaternityMultiSelectDrugPicker());
        map.put(JsonFormConstants.MULTI_SELECT_LIST, new MaternityMultiSelectList());
    }
}

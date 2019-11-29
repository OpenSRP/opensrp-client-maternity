package org.smartregister.maternity.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.maternity.utils.OpdConstants;
import org.smartregister.maternity.widgets.OpdBarcodeFactory;
import org.smartregister.maternity.widgets.OpdEditTextFactory;
import org.smartregister.maternity.widgets.OpdMultiSelectDrugPicker;
import org.smartregister.maternity.widgets.OpdMultiSelectList;

public class OpdFormInteractor extends JsonFormInteractor {

    private static final OpdFormInteractor INSTANCE = new OpdFormInteractor();

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new OpdEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new OpdBarcodeFactory());
        map.put(OpdConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, new OpdMultiSelectDrugPicker());
        map.put(JsonFormConstants.MULTI_SELECT_LIST, new OpdMultiSelectList());
    }
}

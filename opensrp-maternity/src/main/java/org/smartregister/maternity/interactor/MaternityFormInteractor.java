package org.smartregister.maternity.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.maternity.widgets.MaternityBarcodeFactory;
import org.smartregister.maternity.widgets.MaternityEditTextFactory;

public class MaternityFormInteractor extends JsonFormInteractor {


    public static JsonFormInteractor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MaternityFormInteractor();
        }
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new MaternityEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new MaternityBarcodeFactory());
    }
}

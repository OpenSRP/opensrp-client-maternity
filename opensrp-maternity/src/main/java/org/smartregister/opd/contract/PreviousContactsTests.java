package org.smartregister.opd.contract;


import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public interface PreviousContactsTests {

    interface Presenter {

        PreviousContactsTests.View getProfileView();

        void loadPreviousContactsTest(String baseEntityId, String contactNo, String lastContactRecordDate)
                throws ParseException, IOException;
    }

    interface View {
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);
    }
}

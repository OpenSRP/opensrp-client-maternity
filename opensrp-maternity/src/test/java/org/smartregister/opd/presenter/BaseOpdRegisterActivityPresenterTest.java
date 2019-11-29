package org.smartregister.opd.presenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.repository.OpdDiagnosisAndTreatmentFormRepository;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-24
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpdLibrary.class)
public class BaseOpdRegisterActivityPresenterTest {

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private OpdDiagnosisAndTreatmentFormRepository opdDiagnosisAndTreatmentFormRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void startFormShouldPassEntityTableAndBaseEntityIdToActivity() throws JSONException {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdDiagnosisAndTreatmentFormRepository()).thenReturn(opdDiagnosisAndTreatmentFormRepository);

        OpdRegisterActivityContract.View view = Mockito.mock(OpdRegisterActivityContract.View.class);
        OpdRegisterActivityContract.Model model = Mockito.mock(OpdRegisterActivityContract.Model.class);

        BaseOpdRegisterActivityPresenter baseOpdRegisterActivityPresenter = new OpdRegisterActivityPresenter(view, model);

        Mockito.doReturn(new JSONObject()).when(model).getFormAsJson(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.nullable(HashMap.class));

        ReflectionHelpers.setField(baseOpdRegisterActivityPresenter, "viewReference", new WeakReference<OpdRegisterActivityContract.View>(view));
        baseOpdRegisterActivityPresenter.setModel(model);

        baseOpdRegisterActivityPresenter.startForm("check_in.json", "90923-dsfds", "meta", "location-id", null, "ec_child");

        Mockito.verify(view, Mockito.times(1))
                .startFormActivity(Mockito.any(JSONObject.class), Mockito.any(HashMap.class));
    }
}
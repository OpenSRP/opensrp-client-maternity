package org.smartregister.maternity.presenter;

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
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.repository.MaternityOutcomeFormRepository;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(MaternityLibrary.class)
public class BaseMaternityRegisterActivityPresenterTest {

    @Mock
    private MaternityLibrary maternityLibrary;

    @Mock
    private MaternityOutcomeFormRepository maternityOutcomeFormRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void startFormShouldPassEntityTableAndBaseEntityIdToActivity() throws JSONException {
        PowerMockito.mockStatic(MaternityLibrary.class);
        PowerMockito.when(MaternityLibrary.getInstance()).thenReturn(maternityLibrary);
        PowerMockito.when(maternityLibrary.getMaternityOutcomeFormRepository()).thenReturn(maternityOutcomeFormRepository);

        MaternityRegisterActivityContract.View view = Mockito.mock(MaternityRegisterActivityContract.View.class);
        MaternityRegisterActivityContract.Model model = Mockito.mock(MaternityRegisterActivityContract.Model.class);

        BaseMaternityRegisterActivityPresenter baseMaternityRegisterActivityPresenter = new MaternityRegisterActivityPresenter(view, model);

        Mockito.doReturn(new JSONObject()).when(model).getFormAsJson(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.nullable(HashMap.class));

        ReflectionHelpers.setField(baseMaternityRegisterActivityPresenter, "viewReference", new WeakReference<MaternityRegisterActivityContract.View>(view));
        baseMaternityRegisterActivityPresenter.setModel(model);

        baseMaternityRegisterActivityPresenter.startForm("check_in.json", "90923-dsfds", "meta", "location-id", null, "ec_child");

        Mockito.verify(view, Mockito.times(1))
                .startFormActivityFromFormJson(Mockito.any(JSONObject.class), Mockito.any(HashMap.class));
    }
}
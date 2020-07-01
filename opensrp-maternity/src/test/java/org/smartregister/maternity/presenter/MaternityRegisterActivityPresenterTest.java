package org.smartregister.maternity.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterActivityPresenterTest extends BaseTest {

    private BaseMaternityRegisterActivityPresenter presenter;

    @Mock
    private MaternityRegisterActivityContract.View view;

    @Mock
    private MaternityRegisterActivityContract.Model model;

    @Mock
    private MaternityLibrary maternityLibrary;


    @Before
    public void setUp() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        presenter = new TestMaternityRegisterActivityPresenter(view, model);
    }

    @Test
    public void updateInitialsShouldCallViewUpdateInitialsText() {
        String initials = "JR";
        Mockito.doReturn(initials).when(model).getInitials();

        presenter.updateInitials();

        Mockito.verify(view).updateInitialsText(Mockito.eq(initials));
    }

    @Test
    public void saveLanguageShouldCallModelSaveLanguage() {
        String language = "en";

        presenter.saveLanguage(language);

        Mockito.verify(model).saveLanguage(Mockito.eq(language));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }
}
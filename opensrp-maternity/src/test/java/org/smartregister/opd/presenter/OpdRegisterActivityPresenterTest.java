package org.smartregister.opd.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.contract.OpdRegisterActivityContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-19
 */

public class OpdRegisterActivityPresenterTest extends BaseTest {

    private BaseOpdRegisterActivityPresenter presenter;

    @Mock
    private OpdRegisterActivityContract.View view;

    @Mock
    private OpdRegisterActivityContract.Model model;


    @Before
    public void setUp() throws Exception {
        presenter = new TestOpdRegisterActivityPresenter(view, model);
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
}
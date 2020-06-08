package org.smartregister.maternity.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.contract.MaternityProfileOverviewFragmentContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
@RunWith(RobolectricTestRunner.class)
public class MaternityProfileOverviewFragmentPresenterTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private MaternityProfileOverviewFragmentPresenter presenter;

    @Mock
    private MaternityProfileOverviewFragmentContract.View view;

    @Mock
    private MaternityLibrary maternityLibrary;

    private MaternityProfileOverviewFragmentContract.Model model;

    @Before
    public void setUp() throws Exception {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        presenter = Mockito.spy(new MaternityProfileOverviewFragmentPresenter(view));
        MaternityProfileOverviewFragmentContract.Model model = ReflectionHelpers.getField(presenter, "model");
        this.model = Mockito.spy(model);
        ReflectionHelpers.setField(presenter, "model", this.model);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }

    @Test
    public void loadOverviewFactsShouldCallModelFetchLastCheckAndVisit() {
        Mockito.doNothing().when(model).fetchMaternityOverviewDetails(Mockito.eq("bei"), Mockito.any(MaternityProfileOverviewFragmentContract.Model.OnFetchedCallback.class));

        presenter.loadOverviewFacts("bei", Mockito.mock(MaternityProfileOverviewFragmentContract.Presenter.OnFinishedCallback.class));
        Mockito.verify(model, Mockito.times(1))
                .fetchMaternityOverviewDetails(Mockito.eq("bei"), Mockito.any(MaternityProfileOverviewFragmentContract.Model.OnFetchedCallback.class));
    }
}
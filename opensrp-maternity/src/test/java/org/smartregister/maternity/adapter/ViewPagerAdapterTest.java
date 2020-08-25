package org.smartregister.maternity.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ViewPagerAdapterTest {

    @Mock
    private Fragment fragment;

    private ViewPagerAdapter viewPagerAdapter;
    private String title = "Title";

    @Before
    public void setUp() {
        viewPagerAdapter = new ViewPagerAdapter(mock(FragmentManager.class));
        viewPagerAdapter.addFragment(fragment, title);
    }

    @Test
    public void getItemShouldReturnFragment() {
        assertThat(fragment, instanceOf(viewPagerAdapter.getItem(0).getClass()));
    }

    @Test
    public void getCountShouldReturnOne() {
        assertEquals(1, viewPagerAdapter.getCount());
    }

    @Test
    public void getPageTitleShouldReturnValidTitle() {
        assertEquals(title, viewPagerAdapter.getPageTitle(0));
    }
}

package org.smartregister.maternity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.domain.YamlConfigItem;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.helper.MaternityRulesEngineHelper;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.robolectric.util.ReflectionHelpers.setField;

@RunWith(MockitoJUnitRunner.class)
public class MaternityProfileOverviewAdapterTest {

    @Mock
    private List<YamlConfigWrapper> mData;

    @Mock
    private LayoutInflater mInflater;

    @Mock
    private Facts facts;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private MaternityLibrary maternityLibrary;

    private MaternityProfileOverviewAdapter adapter;

    @Before
    @PrepareForTest(LayoutInflater.class)
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        adapter = new MaternityProfileOverviewAdapter(context, mData, facts);
        setField(adapter, "mInflater", mInflater);
    }

    @Test
    public void onCreateViewHolderShouldReturnViewHolder() throws Exception {

        MaternityProfileOverviewAdapter.ViewHolder viewHolder = mock(MaternityProfileOverviewAdapter.ViewHolder.class);
        View view = mock(View.class);
        doReturn(view).when(mInflater).inflate(anyInt(), any(ViewGroup.class), anyBoolean());

        MaternityProfileOverviewAdapter.ViewHolder vh = adapter.onCreateViewHolder(mock(ViewGroup.class), -1);
        Assert.assertThat(viewHolder, instanceOf(vh.getClass()));
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioOne() {

        String group = "group";
        String subGroup = "sub group";
        String template = "test template: {one}";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        MaternityProfileOverviewAdapter.ViewHolder vh = mock(MaternityProfileOverviewAdapter.ViewHolder.class);

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);
        MaternityRulesEngineHelper maternityRulesEngineHelper = mock(MaternityRulesEngineHelper.class);

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);
        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.RED);
        when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);
        when(yamlConfigItem.getTemplate()).thenReturn(template);
        when(yamlConfigItem.getIsRedFont()).thenReturn("yes");
        when(maternityLibrary.getMaternityRulesEngineHelper()).thenReturn(maternityRulesEngineHelper);
        when(maternityRulesEngineHelper.getRelevance(any(Facts.class), anyString())).thenReturn(true);
        when(facts.get("one")).thenReturn("two");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setText(group.toUpperCase());
        verify(sectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(subSectionHeader, times(1)).setText(subGroup.toUpperCase());
        verify(subSectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetailTitle, times(1)).setText(template.replace(": {one}", ""));
        verify(sectionDetails, times(1)).setText(": Two");
        verify(sectionDetailTitle, times(1)).setTextColor(Color.RED);
        verify(sectionDetails, times(1)).setTextColor(Color.RED);
        verify(sectionDetailTitle, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetails, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioTwo() {

        String group = "";
        String subGroup = "";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        MaternityProfileOverviewAdapter.ViewHolder vh = mock(MaternityProfileOverviewAdapter.ViewHolder.class);

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);

        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.BLACK);
        when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setVisibility(View.GONE);
        verify(subSectionHeader, times(1)).setVisibility(View.GONE);
        verify(sectionDetailTitle, times(1)).setTextColor(Color.BLACK);
        verify(sectionDetails, times(1)).setTextColor(Color.BLACK);
        verify(sectionDetailTitle, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetails, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioThree() {

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        MaternityProfileOverviewAdapter.ViewHolder vh = mock(MaternityProfileOverviewAdapter.ViewHolder.class);

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);

        when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        when(yamlConfigWrapper.getGroup()).thenReturn("");
        when(yamlConfigWrapper.getSubGroup()).thenReturn("");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionDetailTitle, times(1)).setVisibility(View.GONE);
        verify(sectionDetails, times(1)).setVisibility(View.GONE);
    }

    @Test
    public void getItemCountShouldReturnOne() {
        int size = 1;
        when(mData.size()).thenReturn(size);
        assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void getTemplateShouldReturnRawData() {
        String rawTemplate = "nothing";
        MaternityProfileOverviewAdapter.Template template = adapter.getTemplate(rawTemplate);
        assertEquals(rawTemplate, template.title);
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", null);
    }
}

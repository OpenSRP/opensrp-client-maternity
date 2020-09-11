package org.smartregister.maternity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.maternity.domain.YamlConfigItem;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.util.StringUtil;

import java.util.ArrayList;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.robolectric.util.ReflectionHelpers.setField;

@RunWith(MockitoJUnitRunner.class)
public class MaternityProfileVisitsAdapterTest {

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private LayoutInflater mInflater;

    @Mock
    private ArrayList<Pair<YamlConfigWrapper, Facts>> items;


    private MaternityProfileVisitsAdapter adapter;

    @Before
    public void setUp() {
        initMocks(this);

        adapter = new MaternityProfileVisitsAdapter(context, items);
        setField(adapter, "mInflater", mInflater);
    }

    @Test
    public void onCreateViewHolderShouldReturnYamlViewHolder() throws Exception {

        ViewGroup parent = mock(ViewGroup.class);
        View view = mock(View.class);
        MaternityProfileVisitsAdapter.YamlViewHolder vh = mock(MaternityProfileVisitsAdapter.YamlViewHolder.class);
        when(mInflater.inflate(anyInt(), any(ViewGroup.class), anyBoolean())).thenReturn(view);

        assertThat(vh, instanceOf(adapter.onCreateViewHolder(parent, -1).getClass()));
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioOne() {

        String group = "group";
        String subGroup = "sub group: {sub_heading}";
        String template = "test template: {one}";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        MaternityProfileVisitsAdapter.YamlViewHolder vh = mock(MaternityProfileVisitsAdapter.YamlViewHolder.class);

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        Facts facts = mock(Facts.class);
        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        Pair<YamlConfigWrapper, Facts> pair = mock(new Pair<>(yamlConfigWrapper, facts).getClass());
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);

        setField(pair, "first", yamlConfigWrapper);
        setField(pair, "second", facts);

        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.RED);
        when(items.get(anyInt())).thenReturn(pair);

        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);
        when(yamlConfigItem.getTemplate()).thenReturn(template);
        when(yamlConfigItem.getHtml()).thenReturn(Boolean.TRUE);
        when(facts.get("sub_heading")).thenReturn("Sub heading");
        when(facts.get("one")).thenReturn("Works");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setText(StringUtil.humanize(group));
        verify(sectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(subSectionHeader, times(1)).setText(StringUtil.humanize(subGroup.replace("{sub_heading}", " Sub heading")));
        verify(subSectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetailTitle, times(1)).setText(template.replace(": {one}", ""));
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioTwo() {

        String group = "group";
        String subGroup = "sub group: {sub_heading}";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        MaternityProfileVisitsAdapter.YamlViewHolder vh = mock(MaternityProfileVisitsAdapter.YamlViewHolder.class);

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        Facts facts = mock(Facts.class);
        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        Pair<YamlConfigWrapper, Facts> pair = mock(new Pair<>(yamlConfigWrapper, facts).getClass());
        setField(pair, "first", yamlConfigWrapper);
        setField(pair, "second", facts);

        when(items.get(anyInt())).thenReturn(pair);

        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(subSectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetailTitle, times(1)).setVisibility(View.GONE);
        verify(sectionDetails, times(1)).setVisibility(View.GONE);
    }

    @Test
    public void getItemCountShouldReturnOne() {
        int size = 1;
        when(items.size()).thenReturn(size);
        assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void getTemplateShouldReturnRawData() {
        String rawTemplate = "nothing";
        MaternityProfileVisitsAdapter.Template template = adapter.getTemplate(rawTemplate);
        assertEquals(rawTemplate, template.title);
    }
}
